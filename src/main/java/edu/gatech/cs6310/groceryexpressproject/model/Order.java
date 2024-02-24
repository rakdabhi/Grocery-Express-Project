package edu.gatech.cs6310.groceryexpressproject.model;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orders", indexes = {
    @Index(name="order_id_index", columnList = "order_id")
})
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="order_id")
    private String orderID;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "customer_account_id")
    private Customer customer;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "store_id")
    private Store store;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "drone_id")
    private Drone drone;
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Line> lines;
    private int orderCost;
    private int orderWeight;
    private int orderDistance;
    private int expectedDeliveryTime;

    public Order(String orderID, Customer customer, Store store, Drone drone, int expectedDeliveryTime) {
        this.orderID = orderID;
        this.customer = customer;
        this.store = store;
        this.drone = drone;
        this.orderCost = 0;
        this.orderWeight = 0;
        this.orderDistance = computeDistance(store.getLocation(), customer.getLocation());
        this.expectedDeliveryTime = expectedDeliveryTime;
    }
    /**
     * Calculates the Euclidean distance between two locations. (Round up to the nearest integer)
     *
     * @param  location1    the first location
     * @param  location2    the second location
     * @return              the Euclidean distance between the two locations (in meters)
     */
    private int computeDistance(Location location1, Location location2) {
        return (int) Math.ceil(Math.sqrt(Math.pow(location1.getX() - location2.getX(), 2) + Math.pow(location1.getY() - location2.getY(), 2)));
    }
    @Override
    public String toString() {
        return "orderID:" + this.orderID;
    }

}
