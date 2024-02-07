package ecommerce.dtos.product;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class ProductDto {
    @NotNull
    private JsonNullable<String> name;
    @NotNull
    private JsonNullable<Double> price;
    @NotNull
    private JsonNullable<Integer> leftInStock;
    private JsonNullable<Long> categoryId;
    private JsonNullable<Long> brandId;
}
