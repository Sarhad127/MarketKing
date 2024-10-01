package org.sarhad.marketking.service;

import org.sarhad.marketking.model.Wishlist;

import java.util.List;

public interface WishesService {
    void addProductToWishlist(Long productId, Long id);
    List<Wishlist> getWishlist(String username);
    List<Wishlist> getWishlistByUserId(Long userId);
    void removeProductFromWishlist(Long productId, String username);
    boolean isProductInWishlist(Long productId, String username);
}
