package edu.gatech.cs6310.groceryexpressproject.model;
import lombok.*;
import jakarta.persistence.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="employees")
public class Employee extends User implements Serializable {
    @Column(name = "tax_id")
    protected String taxID;
    public Employee(String accountID, String firstName, String lastName, String phone, String taxID) {
        super(accountID, firstName, lastName, phone);
        this.taxID = taxID;
    }
}
