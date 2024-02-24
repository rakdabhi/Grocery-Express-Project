
package edu.gatech.cs6310.groceryexpressproject.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "stores", indexes = {
    @Index(name="store_name_index", columnList = "name")
})
public class Store implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", unique = true)
    private String name;
    @Column(name = "revenue")
    private Integer revenue;
    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    private List<Item> items;
    private Integer purchases;
    private Integer overloads;
    private Integer transfers;
    @OneToOne
    @JoinColumn(name = "location_id")
    private Location location;
    private int penalties;
    public Store(String name, Integer revenue, Location location) {
        this.name = name;
        this.revenue = revenue;
        this.location = location;
        this.purchases = 0;
        this.overloads = 0;
        this.transfers = 0;
        this.penalties = 0;
    }
    //Setters and getters
    public Long getStoreID() {
        return this.id;
    }

    /**
     * Information about this store
     * @return - String with information about this store
     */
    @Override
    public String toString() {
        return "name:" + this.name
                + ",revenue:" + this.revenue;
    }
}