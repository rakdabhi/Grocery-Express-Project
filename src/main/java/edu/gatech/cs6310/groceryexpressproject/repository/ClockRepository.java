package edu.gatech.cs6310.groceryexpressproject.repository;
import edu.gatech.cs6310.groceryexpressproject.model.Clock;
import org.springframework.data.repository.CrudRepository;

import java.util.Calendar;

public interface ClockRepository extends CrudRepository<Clock, Long> {

}