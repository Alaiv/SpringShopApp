package ecommerce.specifications;

import ecommerce.dtos.filterDtos.ProductRequestFilterDto;
import ecommerce.models.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Objects;

@Component
public class ProductSpecification {
    public Specification<Product> build(ProductRequestFilterDto filter) {
        return withName(filter.getName())
                .and(withPriceGtOrEt(filter.getPriceFrom()))
                .and(withPriceLtOrEt(filter.getPriceTo()))
                .and(withCategoryId(filter.getCategoryId()))
                .and(withBrandId(filter.getBrandId()))
                .and(withDateGtOrEt(filter.getCreatedFrom()))
                .and(withDateLtOrEt(filter.getCreatedTo()));
    }

    private Specification<Product> withName(String name) {
        return (root, query, cb) -> name == null
                ? cb.conjunction()
                : cb.equal(root.get("name"), name);
    }

    private Specification<Product> withPriceGtOrEt(Double priceFrom) {
        return (root, query, cb) -> priceFrom == null
                ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("price"), priceFrom);
    }

    private Specification<Product> withPriceLtOrEt(Double priceTo) {
        return (root, query, cb) -> priceTo == null
                ? cb.conjunction()
                : cb.lessThanOrEqualTo(root.get("price"), priceTo);
    }

    private Specification<Product> withCategoryId(Long categoryId) {
        return (root, query, cb) -> categoryId == null
                ? cb.conjunction()
                : cb.equal(root.get("category").get("id"), categoryId);
    }

    private Specification<Product> withBrandId(Long brandId) {
        return (root, query, cb) -> brandId == null
                ? cb.conjunction()
                : cb.equal(root.get("brand").get("id"), brandId);
    }

    private Specification<Product> withDateGtOrEt(LocalDate dateFrom) {
        return (root, query, cb) -> dateFrom == null
                ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("createdAt"), dateFrom);
    }

    private Specification<Product> withDateLtOrEt(LocalDate dateTo) {
        return (root, query, cb) -> dateTo == null
                ? cb.conjunction()
                : cb.lessThanOrEqualTo(root.get("createdAt"), dateTo);
    }
}
