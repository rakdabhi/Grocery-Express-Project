package edu.gatech.cs6310.groceryexpressproject.service;

import edu.gatech.cs6310.groceryexpressproject.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.gatech.cs6310.groceryexpressproject.repository.LocationRepository;

@Service
public class LocationService {
    @Autowired
    private LocationRepository locationRepository;

    public boolean isLocationExists(Location location) {
        return locationRepository.findByXAndY(location.getX(), location.getY()) != null;
    }

    public Location createLocation(Location location) {
        return locationRepository.save(location);
    }
    /**
     * Calculates the Euclidean distance between two locations. (Round up to the nearest integer)
     *
     * @param  location1    the first location
     * @param  location2    the second location
     * @return              the Euclidean distance between the two locations (in meters)
     */
    public int computeDistance(Location location1, Location location2) {
        return (int) Math.ceil(Math.sqrt(Math.pow(location1.getX() - location2.getX(), 2) + Math.pow(location1.getY() - location2.getY(), 2)));
    }
}
