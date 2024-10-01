package org.sarhad.marketking.service.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.sarhad.marketking.model.Product;
import org.sarhad.marketking.model.Users;
import org.sarhad.marketking.model.Wishlist;
import org.sarhad.marketking.repository.WishlistRepository;
import org.sarhad.marketking.service.ProductService;
import org.sarhad.marketking.service.UserService;
import org.sarhad.marketking.service.WishesService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class WishesServiceImpl implements WishesService {

    private final WishlistRepository wishlistRepository;
    private final UserService userService;
    private final ProductService productService;

    @Override
    public void addProductToWishlist(Long productId, Long id) {
        Optional<Users> user = userService.findById(id);
        Product product = productService.getProductById(productId);

        if (user.isPresent() && product != null) {
            if (!wishlistRepository.existsByUserAndProduct(user.orElse(null), product)) {
                Wishlist wishlist = new Wishlist(user.orElse(null), product);
                wishlistRepository.save(wishlist);
                System.out.println("Product added to wishlist: " + product.getId());
            } else {
                System.out.println("Product already exists in the wishlist.");
            }
        } else {
            System.out.println("User or product not found.");
        }
    }

    @Override
    public List<Wishlist> getWishlist(String username) {
        Optional<Users> user = userService.findByUsername(username);
        if (user.isEmpty()) {
            return Collections.emptyList();
        }
        return wishlistRepository.findByUser(user.get());
    }

    @Override
    public List<Wishlist> getWishlistByUserId(Long userId) {
        Optional<Users> user = userService.findById(userId);
        return user.map(wishlistRepository::findByUser).orElseGet(ArrayList::new);
    }

    @Override
    public void removeProductFromWishlist(Long productId, String username) {
        Optional<Users> user = userService.findByUsername(username);
        Product product = productService.getProductById(productId);

        if (user.isPresent() && product != null) {
            wishlistRepository.deleteByUserAndProduct(user.orElse(null), product);
        }
    }

    @Override
    public boolean isProductInWishlist(Long productId, String username) {
        Optional<Users> user = userService.findByUsername(username);
        if (user.isEmpty()) {
            return false;
        }
        Product product = productService.getProductById(productId);
        if (product == null) {
            return false;
        }
        return wishlistRepository.existsByUserAndProduct(user.get(), product);
    }
}
