package ecommerce.dtos.orderItems;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDto {

    @NotNull
    private Long baskedId;

    @NotNull
    private Long productId;
}