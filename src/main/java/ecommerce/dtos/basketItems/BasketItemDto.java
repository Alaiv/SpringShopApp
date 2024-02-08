package ecommerce.dtos.basketItems;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasketItemDto {

    @NotNull
    private Long basketId;

    @NotNull
    private Long productId;
}
