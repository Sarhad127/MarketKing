package org.sarhad.marketking.repository;

import org.sarhad.marketking.model.Product;
import org.sarhad.marketking.model.Users;
import org.sarhad.marketking.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByUser(Users user);
    boolean existsByUserAndProduct(Users user, Product product);
    void deleteByUserAndProduct(Users user, Product product);
}
