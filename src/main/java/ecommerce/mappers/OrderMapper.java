package ecommerce.mappers;

import ecommerce.dtos.OrderCreateDto;
import ecommerce.dtos.OrderUpdateDto;
import ecommerce.dtos.OrderVM;
import ecommerce.models.Order;
import org.mapstruct.*;

@Mapper(
        uses = {ReferenceMapper.class, JsonNullableMapper.class},
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy =  ReportingPolicy.IGNORE
)
public abstract class OrderMapper {

    @Mapping(target = "orderItems", source = "orderItemsIds")
    public abstract Order map(OrderCreateDto dto);

    @Mapping(target = "user.id", source = "userId")
    public abstract OrderVM map(Order order);

    public abstract void update(OrderUpdateDto dto, @MappingTarget Order model);
}
