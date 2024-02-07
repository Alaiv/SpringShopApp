package ecommerce.dtos;

import ecommerce.enums.OrderStatuses;
import ecommerce.models.OrderItem;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderVM {
    private Long id;
    private List<OrderItem> orderItems;
    private Long userId;
    private OrderStatuses orderStatus;
}
