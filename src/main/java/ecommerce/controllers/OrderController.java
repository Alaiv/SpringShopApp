package ecommerce.controllers;

import ecommerce.dtos.OrderDto;
import ecommerce.dtos.OrderVM;
import ecommerce.mappers.OrderMapper;
import ecommerce.repositories.BasketRepository;
import ecommerce.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static ecommerce.utils.SearchHelpers.findEntityByIdOrThrow;

@RestController
@RequestMapping("api")
public class OrderController {
    private final OrderRepository orderRepository;
    private final BasketRepository basketRepository;
    private final OrderMapper orderMapper;

    public OrderController(
            OrderMapper orderMapper,
            OrderRepository orderRepository,
            BasketRepository basketRepository
    ) {
        this.orderMapper = orderMapper;
        this.orderRepository = orderRepository;
        this.basketRepository = basketRepository;
    }

    @GetMapping("/orders/search/{basketId}")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderVM> searchOrders(@PathVariable Long basketId) {
        var basket = findEntityByIdOrThrow(basketRepository, basketId);
        var orders = orderRepository.findAllByBasketId(basket.getId());

        return orders.stream()
                .map(orderMapper::map)
                .toList();
    }

    @GetMapping("/orders/find/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OrderVM findOrder(@PathVariable Long id) {
        var order = findEntityByIdOrThrow(orderRepository, id);

        return orderMapper.map(order);
    }

    @PostMapping("/orders/create")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderVM createOrder(@RequestBody OrderDto dto) {
        var order = orderMapper.map(dto);
        orderRepository.save(order);

        return orderMapper.map(order);
    }

    @DeleteMapping("/orders/remove/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeOrder(@PathVariable Long id) {
       var order = findEntityByIdOrThrow(orderRepository, id);

       orderRepository.delete(order);
    }

    @Transactional
    @DeleteMapping("/orders/removeAll/{basketId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAllOrders(@PathVariable Long basketId) {
        findEntityByIdOrThrow(basketRepository, basketId);

        orderRepository.deleteAllByBasketId(basketId);
    }
}