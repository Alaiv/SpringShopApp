package ecommerce.controllers;

import ecommerce.dtos.OrderItemDto;
import ecommerce.dtos.OrderItemVM;
import ecommerce.mappers.OrderItemMapper;
import ecommerce.repositories.BasketRepository;
import ecommerce.repositories.OrderItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static ecommerce.utils.SearchHelpers.findEntityByIdOrThrow;

@RestController
@RequestMapping("api")
public class OrderItemController {
    private final OrderItemRepository orderItemRepository;
    private final BasketRepository basketRepository;
    private final OrderItemMapper orderItemMapper;

    public OrderItemController(
            OrderItemMapper orderItemMapper,
            OrderItemRepository orderItemRepository,
            BasketRepository basketRepository
    ) {
        this.orderItemMapper = orderItemMapper;
        this.orderItemRepository = orderItemRepository;
        this.basketRepository = basketRepository;
    }

    @GetMapping("/orderItem/search/{basketId}")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderItemVM> searchOrderItems(@PathVariable Long basketId) {
        var basket = findEntityByIdOrThrow(basketRepository, basketId);
        var orders = orderItemRepository.findAllByBasketId(basket.getId());

        return orders.stream()
                .map(orderItemMapper::map)
                .toList();
    }

    @GetMapping("/orderItem/find/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OrderItemVM findOrderItem(@PathVariable Long id) {
        var order = findEntityByIdOrThrow(orderItemRepository, id);

        return orderItemMapper.map(order);
    }

    @PostMapping("/orderItem/create")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderItemVM createOrderItem(@RequestBody OrderItemDto dto) {
        var order = orderItemMapper.map(dto);
        orderItemRepository.save(order);

        return orderItemMapper.map(order);
    }

    @DeleteMapping("/orderItem/remove/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeOrderItem(@PathVariable Long id) {
       var order = findEntityByIdOrThrow(orderItemRepository, id);

       orderItemRepository.delete(order);
    }

    @Transactional
    @DeleteMapping("/orderItem/removeAll/{basketId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAllOrderItems(@PathVariable Long basketId) {
        findEntityByIdOrThrow(basketRepository, basketId);

        orderItemRepository.deleteAllByBasketId(basketId);
    }
}