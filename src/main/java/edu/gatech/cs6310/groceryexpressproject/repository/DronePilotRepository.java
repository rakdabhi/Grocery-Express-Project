package edu.gatech.cs6310.groceryexpressproject.repository;
import edu.gatech.cs6310.groceryexpressproject.model.DronePilot;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Sort;
import java.util.List;

public interface DronePilotRepository extends CrudRepository<DronePilot, String> {
    @Override
    List<DronePilot> findAll();
    List<DronePilot> findAll(Sort sort);

    DronePilot findDronePilotByAccountID(String accountID);

    DronePilot findDronePilotByLicenseID(String licenseID);
}
