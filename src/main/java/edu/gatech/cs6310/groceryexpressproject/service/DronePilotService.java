package edu.gatech.cs6310.groceryexpressproject.service;

import edu.gatech.cs6310.groceryexpressproject.model.DronePilot;
import edu.gatech.cs6310.groceryexpressproject.repository.DronePilotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class DronePilotService {
    @Autowired
    private DronePilotRepository dronePilotRepository;
    @Autowired
    CacheManager cacheManager;
    public boolean isPilotExists(String accountID) {
        return dronePilotRepository.findDronePilotByAccountID(accountID) != null;
    }

    @CacheEvict("Pilots")
    public DronePilot createDronePilot(DronePilot dronePilot) {
        if (isPilotExists(dronePilot.getAccountID())) {
            System.out.println("ERROR:pilot_identifier_already_exists");
            return null;
        }
        if(dronePilotRepository.findDronePilotByLicenseID(dronePilot.getLicenseID()) != null) {
            System.out.println("ERROR:pilot_license_already_exists");
            return null;
        }
        return dronePilotRepository.save(dronePilot);
    }

    @Cacheable("Pilots")
    public List<DronePilot> getAllDronePilots() {
        return dronePilotRepository.findAll(Sort.by(Sort.Direction.ASC, "accountID"));
    }
    @Cacheable(value="Pilot", key="#accountID", condition = "#accountID != null")
    public DronePilot getDronePilot(String accountID) {
        return dronePilotRepository.findDronePilotByAccountID(accountID);
    }

    @CacheEvict(value="Pilot", key="#dronePilot.accountID", condition = "#dronePilot.accountID != null")
    public DronePilot updateDronePilot(DronePilot dronePilot) {
        Objects.requireNonNull(cacheManager.getCache("Pilots")).clear();
        return dronePilotRepository.save(dronePilot);
    }

    @CacheEvict(value="Pilot", key="#pilot.accountID", condition = "#pilot.accountID != null")
    public void incrementPilotExperience(DronePilot pilot) {
        Objects.requireNonNull(cacheManager.getCache("Pilots")).clear();
        pilot.setExperience(pilot.getExperience() + 1);
        dronePilotRepository.save(pilot);
    }

    @CacheEvict(value={"Pilots", "Pilot"}, allEntries = true)
    public void deleteDronePilot(DronePilot pilot) {
        dronePilotRepository.delete(pilot);
    }
}
