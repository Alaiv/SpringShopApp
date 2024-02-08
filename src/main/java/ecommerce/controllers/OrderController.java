package ecommerce.controllers;

import ecommerce.dtos.filterDtos.OrderRequestFilterDto;
import ecommerce.dtos.order.OrderCreateDto;
import ecommerce.dtos.order.OrderVM;
import ecommerce.enums.OrderStatuses;
import ecommerce.mappers.OrderMapper;
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
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderSpecification specification;

    @PostMapping("/orders/search")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderVM> search(
            @RequestBody OrderRequestFilterDto filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        var spec = specification.build(filter);
        var products = orderRepository.findAll(spec, PageRequest.of(page - 1, limit));

        var result = products
                .map(orderMapper::map)
                .toList();

        return result;
    }

    @GetMapping("/orders/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OrderVM find(@PathVariable Long id) {
        var product = findEntityByIdOrThrow(orderRepository, id);

        return orderMapper.map(product);
    }

    @PostMapping("/orders/create")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderVM create(@Valid @RequestBody OrderCreateDto dto) {
        var orderModel = orderMapper.map(dto);
        orderRepository.save(orderModel);

        return orderMapper.map(orderModel);
    }

    @DeleteMapping("/orders/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long id) {
        var product = findEntityByIdOrThrow(orderRepository, id);

        orderRepository.delete(product);
    }

    @PutMapping("/orders/{id}/addOrderItem/{orderItemId}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public OrderVM update(@PathVariable Long id, @PathVariable Long orderItemId) {
        var order = findEntityByIdOrThrow(orderRepository, id);
        var orderItem = findEntityByIdOrThrow(orderItemRepository, orderItemId);

        order.getOrderItems().add(orderItem);
        orderRepository.save(order);

        return orderMapper.map(order);
    }

    @PutMapping("/orders/{id}/changeOrderStatus")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public OrderVM update(@PathVariable Long id, @RequestParam OrderStatuses status) {
        var order = findEntityByIdOrThrow(orderRepository, id);
        order.setOrderStatus(status);

        orderRepository.save(order);

        return orderMapper.map(order);
    }
}
