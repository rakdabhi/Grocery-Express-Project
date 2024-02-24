package edu.gatech.cs6310.groceryexpressproject.repository;
import java.util.List;
import edu.gatech.cs6310.groceryexpressproject.model.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
    @Override
    List<Order> findAll();
    List<Order> findAll(Sort sort);
    List<Order> findOrdersByStore(Store store, Sort sort);
    Order findOrderByOrderIDAndStore(String orderID, Store store);
}
