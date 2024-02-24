package edu.gatech.cs6310.groceryexpressproject.repository;
import java.util.List;
import edu.gatech.cs6310.groceryexpressproject.model.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepository extends CrudRepository<Item, Long> {
    List<Item> findItemsByStore(Store store, Sort sort);
    Item findItemByNameAndStore(String itemName, Store store);
}
