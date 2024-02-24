package edu.gatech.cs6310.groceryexpressproject.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "drones", indexes = {
    @Index(name="drone_id_index", columnList = "drone_id")
})
public class Drone implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="drone_id")
    private String droneID;
    @Column(name="weight_capacity")
    private int weightCapacity;
    @Column(name="remaining_weight")
    private int remainingWeight;
    @Column(name="overloads")
    private int overloads;
    @OneToOne(mappedBy = "drone")
    private DronePilot pilot;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "store_id")
    private Store store;
    private String storeName;
    @OneToMany(mappedBy = "drone", fetch = FetchType.EAGER)
    private List<Order> orders;
    /*
     * NOTE: Battery charge has unit 'C' (you can think of 1C as 1/100 Watt-hour)
    */
    @Column(name="fuel_capacity")
    private int fuelCapacity; // fuel capacity of the drone (in C)
    @Column(name="remaining_fuel")
    private int remainingFuel; // remaining fuel of the drone (in C)
    @Column(name="refuel_rate")
    private int refuelRate; // fuel refuel rate of the drone (in C per minute)
    @Column(name="fuel_consumption_rate")
    private int fuelConsumptionRate; // fuel consumption rate of the drone (in C per meter)
    @Column(name="speed")
    private int speed; // speed of the drone (in meters per minutes)
    @Column(name="max_flight_range")
    private int maxFlightRange; // maximum range of the drone (in meters)

    public Drone(String droneID, int weightCapacity, Store store, int fuelCapacity, int refuelRate, int fuelConsumptionRate, int speed) {
        this.droneID = droneID;
        this.weightCapacity = weightCapacity;
        this.remainingWeight = weightCapacity;
        this.pilot = null;
        this.overloads = 0;
        this.store = store;
        this.storeName = store.getName();
        this.fuelCapacity = fuelCapacity;
        this.remainingFuel = fuelCapacity;
        this.refuelRate = refuelRate;
        this.fuelConsumptionRate = fuelConsumptionRate;
        this.speed = speed;
        this.maxFlightRange = fuelCapacity / refuelRate;
    }

    public Drone(String droneID, String weightCapacity, Store store, String fuelCapacity, String refuelRate, String fuelConsumptionRate, String speed) {
        this(droneID, Integer.parseInt(weightCapacity), store, Integer.parseInt(fuelCapacity), Integer.parseInt(refuelRate), Integer.parseInt(fuelConsumptionRate), Integer.parseInt(speed));
    }

    /**
     * Information about this drone
     * @return String representing information about this drone
     */
    @Override
    public String toString() {
        String retString = "";
        retString = "droneID:" + this.droneID
                + ",total_cap:" + this.weightCapacity
                + ",num_orders:" + orders.size()
                + ",remaining_cap:" + this.remainingWeight
                + ",fuel_cap:" + this.fuelCapacity
                + ",remaining_fuel:" + this.remainingFuel
                + ",refuel_rate:" + this.refuelRate
                + ",fuel_consumption_rate:" + this.fuelConsumptionRate
                + ",speed:" + this.speed;

        if (this.pilot != null) {
            String pilotField = ",flown_by:" + pilot.getFullName();
            retString += pilotField;
        }

        return retString;
    }

}
