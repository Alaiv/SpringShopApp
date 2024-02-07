package ecommerce.controllers;

import ecommerce.dtos.OrderVM;
import ecommerce.mappers.OrderMapper;
import ecommerce.repositories.OrderRepository;
import org.hibernate.query.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderMapper orderMapper;

    @GetMapping("/orders/{baskedId}")
    public List<OrderVM> search(@PathVariable Long baskedId) {
        var orders = orderRepository.findAllByBasketId(baskedId);

        return orders.stream()
                .map(o -> orderMapper.map(o))
                .toList();
    }
}
