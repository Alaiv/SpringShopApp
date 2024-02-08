package ecommerce.mappers;

import ecommerce.dtos.OrderItemVM;
import ecommerce.models.BasketItem;
import ecommerce.models.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = {ReferenceMapper.class, ProductMapper.class, BasketItem.class},
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class OrderItemMapper {

    public abstract OrderItem map(BasketItem basketItem);
    public abstract OrderItemVM map(OrderItem orderItem);
}
