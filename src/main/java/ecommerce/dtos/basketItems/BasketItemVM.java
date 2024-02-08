package ecommerce.dtos.basketItems;

import ecommerce.dtos.product.ProductVM;
import ecommerce.models.Basket;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BasketItemVM {
    private Long id;
    private Basket basket;
    private ProductVM product;
    private LocalDate createdAt;
}
