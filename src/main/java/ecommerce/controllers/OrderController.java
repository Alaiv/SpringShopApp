package ecommerce.controllers;

import ecommerce.dtos.filterDtos.OrderRequestFilterDto;
import ecommerce.dtos.order.OrderCreateDto;
import ecommerce.dtos.order.OrderVM;
import ecommerce.enums.OrderStatuses;
import ecommerce.exceptions.BadRequestException;
import ecommerce.mappers.OrderItemMapper;
import ecommerce.mappers.OrderMapper;
import ecommerce.repositories.BasketItemRepository;
import ecommerce.repositories.BasketRepository;
import ecommerce.repositories.OrderItemRepository;
import ecommerce.repositories.OrderRepository;
import ecommerce.specifications.OrderSpecification;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static ecommerce.utils.SearchHelpers.findEntityByIdOrThrow;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {
    private final OrderRepository orderRepository;
    private final BasketItemRepository basketItemRepository;
    private final BasketRepository basketRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderSpecification orderSpecification;
    private final OrderItemMapper itemMapper;

    @PostMapping("/orders/search")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderVM> search(
            @RequestBody OrderRequestFilterDto filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        var spec = orderSpecification.build(filter);
        var orders = orderRepository.findAll(spec, PageRequest.of(page - 1, limit));

        var result = orders
                .map(orderMapper::map)
                .toList();

        return result;
    }

    @GetMapping("/orders/find/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OrderVM find(@PathVariable Long id) {
        var product = findEntityByIdOrThrow(orderRepository, id);

        return orderMapper.map(product);
    }

    @PostMapping("/orders/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public OrderVM create(@Valid @RequestBody OrderCreateDto dto) {
        var basket = findEntityByIdOrThrow(basketRepository, dto.getBasketId());
        var basketItems = basketItemRepository.findAllByBasketId(basket.getId());

        if (basketItems.isEmpty()) throw new BadRequestException("Can't create order with empty basket.");

        var orderItems = basketItems.stream()
                .map(itemMapper::map)
                .toList();

        var order = orderMapper.map(dto);
        order.setOrderItems(orderItems);
        order.setOrderStatus(OrderStatuses.CREATED);

        orderRepository.save(order);
        basketItemRepository.deleteAllByBasketId(basket.getId());

        return orderMapper.map(order);
    }

    @DeleteMapping("/orders/remove/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long id) {
        var product = findEntityByIdOrThrow(orderRepository, id);

        orderRepository.delete(product);
    }

    @PutMapping("/orders/{id}/update")
    @ResponseStatus(HttpStatus.OK)
    public OrderVM update(@PathVariable Long id, @RequestParam(name = "status") OrderStatuses status) {
        var order = findEntityByIdOrThrow(orderRepository, id);
        order.setOrderStatus(status);

        orderRepository.saveAndFlush(order);

        return orderMapper.map(order);
    }
}
