package ecommerce.dtos;

import lombok.Getter;
import lombok.Setter;
import ecommerce.models.Brand;
import ecommerce.models.Category;

@Getter
@Setter
public class ProductVM {
    private Long id;
    private String name;
    private double price;
    private Brand brand;
    private Category category;
    private int leftInStock;
}
