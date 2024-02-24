package edu.gatech.cs6310.groceryexpressproject.repository;

import edu.gatech.cs6310.groceryexpressproject.model.Location;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LocationRepository extends CrudRepository<Location, Long> {
    List<Location> findAll();

    Location findByXAndY(int x, int y);
}

