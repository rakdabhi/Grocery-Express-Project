package edu.gatech.cs6310.groceryexpressproject.model;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "items", indexes = {
    @Index(name="item_name_index", columnList = "name")
})
public class Item implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "weight")
    private int weight;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "store_id")
    private Store store;

    public Item(String name, int weight, Store store) {
        this.name = name;
        this.weight = weight;
        this.store = store;
    }

    public Item(String name, String weight, Store store) {
        this(name, Integer.parseInt(weight), store);
    }

    /**
     * Information about this item
     * @return String representing information about this item
     */
    @Override
    public String toString() {
        return this.name + "," + this.weight;
    }
}
