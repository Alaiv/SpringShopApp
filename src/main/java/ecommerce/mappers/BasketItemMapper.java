package ecommerce.mappers;

import ecommerce.dtos.basketItems.BasketItemDto;
import ecommerce.dtos.basketItems.BasketItemVM;
import ecommerce.models.BasketItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = {ReferenceMapper.class, ProductMapper.class},
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class BasketItemMapper {
    @Mapping(target = "basket", source = "basketId")
    @Mapping(target = "product", source = "productId")
    public abstract BasketItem map(BasketItemDto dto);
    public abstract BasketItemVM map(BasketItem basketItem);
}
