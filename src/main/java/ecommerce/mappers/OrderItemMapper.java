package ecommerce.mappers;

import ecommerce.dtos.OrderItemDto;
import ecommerce.dtos.OrderItemVM;
import ecommerce.models.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = {ReferenceMapper.class, ProductMapper.class},
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class OrderItemMapper {
    @Mapping(target = "basket", source = "baskedId")
    @Mapping(target = "product", source = "productId")
    public abstract OrderItem map(OrderItemDto dto);
    public abstract OrderItemVM map(OrderItem orderItem);
}
