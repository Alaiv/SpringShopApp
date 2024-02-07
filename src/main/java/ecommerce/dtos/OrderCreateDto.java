package ecommerce.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderCreateDto {
    @NotNull
    private List<Long> orderItemIds;
    @NotNull
    private Long userId;
}
