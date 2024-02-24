package edu.gatech.cs6310.groceryexpressproject.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "locations")
public class Location implements Serializable {
    @CsvBindByName(column = "location_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CsvBindByName(column = "x")
    @Column(name="x")
    private int x;
    @CsvBindByName(column = "y")
    @Column(name="y")
    private int y;
    @CsvIgnore
    @OneToOne(mappedBy = "location")
    private Store store;
    @CsvIgnore
    @OneToOne(mappedBy = "location", cascade = CascadeType.REMOVE)
    private Customer customer;
    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
