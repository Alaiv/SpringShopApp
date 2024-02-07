package ecommerce.repositories;

import ecommerce.models.Basket;
import ecommerce.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByBasketId(Long basketId);
    void deleteAllByBasketId(Long basketId);
}
