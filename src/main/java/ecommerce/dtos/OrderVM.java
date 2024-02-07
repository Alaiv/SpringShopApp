package ecommerce.dtos;

import ecommerce.models.Basket;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class OrderVM {
    private Long id;
    private Basket basket;
    private ProductVM product;
    private LocalDate createdAt;
}
