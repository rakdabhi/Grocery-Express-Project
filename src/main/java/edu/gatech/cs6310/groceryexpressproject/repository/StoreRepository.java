
package edu.gatech.cs6310.groceryexpressproject.repository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import edu.gatech.cs6310.groceryexpressproject.model.*;

public interface StoreRepository extends CrudRepository<Store, Long> {
    @Override
    List<Store> findAll();
    List<Store> findAll(Sort sort);
    Store findByName(String name);
}
