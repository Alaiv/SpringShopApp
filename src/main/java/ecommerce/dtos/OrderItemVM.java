package ecommerce.dtos;

import ecommerce.dtos.order.OrderVM;
import ecommerce.dtos.product.ProductVM;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class OrderItemVM {
        private Long id;
        private OrderVM order;
        private ProductVM product;
        private LocalDate createdAt;
}
