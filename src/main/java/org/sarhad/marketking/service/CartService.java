package org.sarhad.marketking.service;

import org.sarhad.marketking.model.Cart;
import org.sarhad.marketking.model.CartItem;
import java.util.List;

public interface CartService {
    Cart getCart(String sessionId);
    void addToCart(String sessionId, Long productId, int quantity);
    double calculateTotalAmount(List<CartItem> cartItems);
    List<CartItem> getCartItems(String sessionId);
    void removeProduct(String sessionId, Long productId);
    void updateProductQuantity(String sessionId, Long productId, int quantity);
    void decreaseProductQuantity(String sessionId, Long productId);
    void increaseProductQuantity(String sessionId, Long productId);
    void clearCart(String sessionId);
}
