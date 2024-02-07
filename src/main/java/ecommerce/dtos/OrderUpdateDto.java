package ecommerce.dtos;

import ecommerce.enums.OrderStatuses;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderUpdateDto {
    private Long orderItemId;
    private OrderStatuses orderStatus;
}
