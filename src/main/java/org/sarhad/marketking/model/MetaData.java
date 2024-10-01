package org.sarhad.marketking.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class MetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String createdAt;
    private String updatedAt;
    private String barcode;
    private String qrCode;

    @OneToOne(mappedBy = "meta")
    private Product product;
}
