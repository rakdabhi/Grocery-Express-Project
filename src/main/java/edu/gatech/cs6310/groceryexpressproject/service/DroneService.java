package edu.gatech.cs6310.groceryexpressproject.service;

import edu.gatech.cs6310.groceryexpressproject.model.*;
import edu.gatech.cs6310.groceryexpressproject.repository.DroneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DroneService {
    @Autowired
    private DroneRepository droneRepository;

    public boolean isDroneExists(String droneID, String storeID) {
        return droneRepository.findDroneByDroneIDAndStoreName(droneID, storeID) != null;
    }
    public Drone createDrone(Drone drone) {
        return droneRepository.save(drone);
    }
    public List<Drone> getAllDrones(Store store) {
        return droneRepository.findDronesByStore(store, Sort.by(Sort.Direction.ASC, "droneID"));
    }
    public Drone getDrone(String droneID, String storeName){
        return droneRepository.findDroneByDroneIDAndStoreName(droneID, storeName);
    }
    public void displayDrones(Store store) {
        List<Drone> drones = getAllDrones(store);
        for (Drone drone : drones) {
            System.out.println(drone.toString());
        }
        System.out.println("OK:display_completed");
    }

    public boolean canCarryItem(Drone drone, Item item, int quantity) {
        return drone.getRemainingWeight() >= item.getWeight() * quantity;
    }

    public void updateDroneRemainingWeight(Drone drone, Item item, int quantity) {
        drone.setRemainingWeight(drone.getRemainingWeight() - item.getWeight() * quantity);
        droneRepository.save(drone);
    }

    public void increaseDroneRemainingWeight(Drone drone, Order order) {
        drone.setRemainingWeight(drone.getRemainingWeight() + order.getOrderWeight());
        droneRepository.save(drone);
    }


    /**
     * Decrements the fuel remaining after delivering an order
     * @return true if remaining trips is decremented, else false
     */
    public boolean decrementFuel(Drone drone, int fuelConsumed) {
        drone.setRemainingFuel(drone.getRemainingFuel() - fuelConsumed);
        droneRepository.save(drone);
        return true;
    }

    public void decreaseDroneRemainingWeight(Drone drone, Order order) {
        drone.setRemainingWeight(drone.getRemainingWeight() - order.getOrderWeight());
        droneRepository.save(drone);
    }
}
