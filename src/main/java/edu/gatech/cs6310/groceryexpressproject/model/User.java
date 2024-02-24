package edu.gatech.cs6310.groceryexpressproject.model;
import com.opencsv.bean.CsvBindByName;
import lombok.*;
import jakarta.persistence.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Entity
@Table(name = "users")
public class User implements Serializable {
    @CsvBindByName(column = "account_ID")
    @Id
    @Column(name = "account_ID")
    protected String accountID;
    @CsvBindByName(column = "first_name")
    @Column(name = "first_name")
    protected String firstName;
    @CsvBindByName(column = "last_name")
    @Column(name = "last_name")
    protected String lastName;
    @CsvBindByName(column = "phone_number")
    @Column(name = "phone_number")
    protected String phone;

    public User(String firstName, String lastName, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public String getFullName() {
        return this.firstName + "_" + this.lastName;
    }
}
