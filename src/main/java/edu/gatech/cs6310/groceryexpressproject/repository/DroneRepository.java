package edu.gatech.cs6310.groceryexpressproject.repository;
import edu.gatech.cs6310.groceryexpressproject.model.Drone;
import edu.gatech.cs6310.groceryexpressproject.model.Store;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Sort;
import java.util.List;

public interface DroneRepository extends CrudRepository<Drone, Long>{
    @Override
    List<Drone> findAll();
    List<Drone> findAll(Sort sort);
    List<Drone> findDronesByStore(Store store, Sort sort);
    Drone findDroneByDroneIDAndStoreName(String droneID, String storeName);
}
