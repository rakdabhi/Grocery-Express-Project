package edu.gatech.cs6310.groceryexpressproject.repository;
import java.util.List;
import edu.gatech.cs6310.groceryexpressproject.model.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

public interface LineRepository extends CrudRepository<Line, Long> {
    @Override
    List<Line> findAll();
    List<Line> findAll(Sort sort);
    List<Line> findItemsByOrder(Order order, Sort sort);
    Line findLineByOrderAndItemName(Order order, String itemName);

    List<Line> findLinesByOrder(Order order);
}
