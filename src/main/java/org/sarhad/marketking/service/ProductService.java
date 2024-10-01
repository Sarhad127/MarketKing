package org.sarhad.marketking.service;

import org.sarhad.marketking.model.Product;
import org.sarhad.marketking.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    void fetchAndStoreProducts();
    List<Product> getAllProducts();
    Product getProductById(Long id);
    void addReview(Long productId, Review review);
    Page<Product> getProducts(Pageable pageable);
}
