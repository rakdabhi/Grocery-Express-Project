package edu.gatech.cs6310.groceryexpressproject.model;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "lines")
public class Line implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    private String itemName;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    @Column(name = "quantity")
    private int quantity;
    @Column(name = "price")
    private int price;
    @Column(name = "line_weight")
    private int lineWeight;
    @Column(name = "line_cost")
    private int lineCost;

    public Line(Order order, Item item, int quantity, int price) {
        this.order = order;
        this.item = item;
        this.itemName = item.getName();
        this.quantity = quantity;
        this.price = price;
        this.lineWeight = item.getWeight() * quantity;
        this.lineCost = price * quantity;
    }

    @Override
    public String toString() {
        return "item_name:" + itemName +
                ",total_quantity:" + quantity +
                ",total_cost:" + lineCost +
                ",total_weight:" + lineWeight;
    }
}
