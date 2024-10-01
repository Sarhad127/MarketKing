package org.sarhad.marketking.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Cart cart;

    private int quantity;

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", productTitle='" + product.getTitle() + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
