package ecommerce.mappers;

import ecommerce.dtos.OrderDto;
import ecommerce.dtos.OrderVM;
import ecommerce.models.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = {ReferenceMapper.class, ProductMapper.class},
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class OrderMapper {
    @Mapping(target = "basket", source = "baskedId")
    @Mapping(target = "product", source = "productId")
    public abstract Order map(OrderDto dto);
    public abstract OrderVM map(Order order);
}
