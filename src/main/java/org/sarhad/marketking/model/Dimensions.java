package org.sarhad.marketking.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Entity
@Data
public class Dimensions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double width;
    private double height;
    private double depth;

    @OneToOne(mappedBy = "dimensions")
    private Product product;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dimensions dimensions)) return false;
        return Objects.equals(id, dimensions.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
