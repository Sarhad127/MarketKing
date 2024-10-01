package org.sarhad.marketking.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Images {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
