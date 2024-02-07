package ecommerce.dtos.brand;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandDTO {
    @NotNull
    private String name;
}
