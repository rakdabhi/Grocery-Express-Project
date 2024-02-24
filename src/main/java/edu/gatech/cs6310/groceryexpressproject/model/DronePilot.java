package edu.gatech.cs6310.groceryexpressproject.model;
import lombok.*;
import jakarta.persistence.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "drones_pilots")
public class DronePilot extends Employee implements Serializable {
    @Column(name = "license_id")
    private String licenseID;
    @Column(name = "experience")
    private Integer experience;
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "drone_id")
    private Drone drone;
    public DronePilot(String accountID, String firstName, String lastName, String phone,
                      String taxID, String licenseID, int experience) {
        super(accountID, firstName, lastName, phone, taxID);
        this.licenseID = licenseID;
        this.experience = experience;
    }

    public DronePilot(String accountID, String firstName, String lastName, String phone,
                      String taxID, String licenseID, String experience) {
        this(accountID, firstName, lastName, phone, taxID, licenseID, Integer.parseInt(experience));
    }

    /**
     * Information about this drone pilot
     * @return String representing information about this drone pilot
     */
    @Override
    public String toString() {
        return "name:" + this.getFullName() +
                ",phone:" + this.phone +
                ",taxID:" + this.taxID +
                ",licenseID:" + this.licenseID +
                ",experience:" + this.experience;
    }

}
