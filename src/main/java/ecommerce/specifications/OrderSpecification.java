package ecommerce.specifications;

import ecommerce.dtos.filterDtos.OrderRequestFilterDto;
import ecommerce.enums.OrderStatuses;
import ecommerce.models.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class OrderSpecification {
    public Specification<Order> build(OrderRequestFilterDto filter) {
        return withUserId(filter.getUserId())
                .and(withStatus(filter.getOrderStatus()));
    }

    private Specification<Order> withUserId(Long userId) {
        return (root, query, cb) -> userId == null
                ? cb.conjunction()
                : cb.equal(root.get("user").get("id"), userId);
    }

    private Specification<Order> withStatus(OrderStatuses status) {
        return (root, query, cb) -> status == null
                ? cb.conjunction()
                : cb.equal(root.get("orderStatus"), status);
    }
}
