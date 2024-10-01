package org.sarhad.marketking.repository;

import org.sarhad.marketking.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
