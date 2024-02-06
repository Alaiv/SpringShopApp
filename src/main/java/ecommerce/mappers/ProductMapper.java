package ecommerce.mappers;

import ecommerce.dtos.ProductDto;
import ecommerce.dtos.ProductVM;
import ecommerce.models.Product;
import org.mapstruct.*;

@Mapper(
        uses = {ReferenceMapper.class, JsonNullableMapper.class},
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy =  ReportingPolicy.IGNORE
)
public abstract class ProductMapper {
    @Mapping(target = "category", source = "categoryId")
    @Mapping(target = "brand", source = "brandId")
    public abstract Product map(ProductDto productDto);

    public abstract ProductVM map(Product product);

    public abstract void update(ProductDto productDto, @MappingTarget Product model);
}
