package ecommerce.dtos.filterDtos;

import ecommerce.enums.OrderStatuses;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequestFilterDto {
    @NotNull
    private Long userId;
    private OrderStatuses orderStatus;
}
