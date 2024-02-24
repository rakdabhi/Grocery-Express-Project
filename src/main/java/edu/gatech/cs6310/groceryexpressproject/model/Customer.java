package edu.gatech.cs6310.groceryexpressproject.model;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvRecurse;
import lombok.*;
import jakarta.persistence.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customers")
public class Customer extends User implements Serializable {
    @CsvBindByName(column = "rating")
    @Column(name = "rating")
    private Integer rating;
    @CsvBindByName(column = "credit")
    @Column(name = "credit")
    private Integer credit;
    @CsvBindByName(column = "pending_cost")
    @Column(name = "pending_cost")
    private Integer pendingCost;

    @CsvRecurse
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "location_id")
    private Location location;

    public Customer(String accountID, String firstName, String lastName, String phone, int rating, int credit, Location location) {
        super(accountID, firstName, lastName, phone);
        this.rating = rating;
        this.credit = credit;
        this.pendingCost = 0;
        this.location = location;
    }

    /**
     * String representing information about this customer
     * @return - a String representing information about this customer
     */
    @Override
    public String toString() {
        return "name:" + this.getFullName() +
                ",phone:" + this.phone +
                ",rating:" + this.rating +
                ",credit:" + this.credit;
    }
}
