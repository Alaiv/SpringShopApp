package ecommerce.mappers;

import ecommerce.dtos.order.OrderCreateDto;
import ecommerce.dtos.order.OrderUpdateDto;
import ecommerce.dtos.order.OrderVM;
import ecommerce.models.Order;
import org.mapstruct.*;

@Mapper(
        uses = {ReferenceMapper.class, JsonNullableMapper.class, OrderItemMapper.class},
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy =  ReportingPolicy.IGNORE
)
public abstract class OrderMapper {

    @Mapping(target = "orderItems", source = "orderItemIds")
    public abstract Order map(OrderCreateDto dto);

    @Mapping(target = "userId", source = "user.id")
    public abstract OrderVM map(Order order);

    public abstract void update(OrderUpdateDto dto, @MappingTarget Order model);
}
