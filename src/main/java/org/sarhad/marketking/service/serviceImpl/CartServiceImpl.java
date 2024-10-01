package org.sarhad.marketking.service.serviceImpl;

import lombok.AllArgsConstructor;
import org.sarhad.marketking.model.Cart;
import org.sarhad.marketking.model.CartItem;
import org.sarhad.marketking.model.Product;
import org.sarhad.marketking.repository.CartRepository;
import org.sarhad.marketking.repository.ProductRepository;
import org.sarhad.marketking.service.CartService;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Override
    public Cart getCart(String sessionId) {
        return cartRepository.findBySessionId(sessionId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setSessionId(sessionId);
                    return cartRepository.save(newCart);
                });
    }

    @Override
    public void addToCart(String sessionId, Long productId, int quantity) {
        Cart cart = getCart(sessionId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setCart(cart);
            cart.getItems().add(cartItem);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }
        cartRepository.save(cart);
    }

    @Override
    public double calculateTotalAmount(List<CartItem> cartItems) {
        return cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }

    @Override
    public List<CartItem> getCartItems(String sessionId) {
        return cartRepository.findBySessionId(sessionId)
                .map(Cart::getItems)
                .orElse(Collections.emptyList());
    }

    @Override
    public void updateProductQuantity(String sessionId, Long productId, int change) {
        Cart cart = getCart(sessionId);
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (cartItem != null) {
            int newQuantity = cartItem.getQuantity() + change;
            if (newQuantity > 0) {
                cartItem.setQuantity(newQuantity);
            } else {
                cart.getItems().remove(cartItem);
            }
            cartRepository.save(cart);
        }
    }

    @Override
    public void removeProduct(String sessionId, Long productId) {
        Cart cart = getCart(sessionId);
        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        cartRepository.save(cart);
    }

    @Override
    public void decreaseProductQuantity(String sessionId, Long productId) {
        Cart cart = getCart(sessionId);
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        if (cartItem.getQuantity() > 1) {
            cartItem.setQuantity(cartItem.getQuantity() - 1);
        } else {
            cart.getItems().remove(cartItem);
        }
        cartRepository.save(cart);
    }

    @Override
    public void increaseProductQuantity(String sessionId, Long productId) {
        Cart cart = getCart(sessionId);
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found in cart"));
        cartItem.setQuantity(cartItem.getQuantity() + 1);
        cartRepository.save(cart);
    }

    @Override
    public void clearCart(String sessionId) {
        Cart cart = getCart(sessionId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
