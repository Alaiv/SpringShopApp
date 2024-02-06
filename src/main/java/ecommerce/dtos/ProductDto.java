package ecommerce.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {
    @NotNull
    private String name;
    @NotNull
    private double price;
    @NotNull
    private int leftInStock;
    private Long categoryId;
    private Long brandId;
}
