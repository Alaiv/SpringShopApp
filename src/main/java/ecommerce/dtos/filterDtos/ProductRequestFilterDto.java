package ecommerce.dtos.filterDtos;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestFilterDto {
    private String name;
    private Double priceFrom;
    private Double priceTo;
    private Long categoryId;
    private Long brandId;
    private LocalDate createdFrom;
    private LocalDate createdTo;
}
