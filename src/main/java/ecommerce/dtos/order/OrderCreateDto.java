package ecommerce.dtos.order;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderCreateDto {
    @NotNull
    private Long basketId;
    @NotNull
    private Long userId;
}
