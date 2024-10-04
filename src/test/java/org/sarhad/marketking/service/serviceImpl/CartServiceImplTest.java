package org.sarhad.marketking.service.serviceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sarhad.marketking.model.Cart;
import org.sarhad.marketking.model.CartItem;
import org.sarhad.marketking.model.Product;
import org.sarhad.marketking.repository.CartRepository;
import org.sarhad.marketking.repository.ProductRepository;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ActiveProfiles("test")
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        cart = new Cart();
        cart.setSessionId("sessionId");
        cart.setItems(new ArrayList<>());

        product = new Product();
        product.setId(1L);
        product.setPrice(10.0);
    }

    @Test
    void testGetCart_WhenCartExists() {
        when(cartRepository.findBySessionId("sessionId")).thenReturn(Optional.of(cart));

        Cart result = cartService.getCart("sessionId");

        assertEquals(cart, result);
        verify(cartRepository, times(1)).findBySessionId("sessionId");
    }

    @Test
    void testGetCart_WhenCartDoesNotExist() {
        when(cartRepository.findBySessionId("sessionId")).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cart result = cartService.getCart("sessionId");

        assertNotNull(result);
        assertEquals("sessionId", result.getSessionId());
        verify(cartRepository, times(1)).findBySessionId("sessionId");
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testAddToCart_WhenProductDoesNotExist() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            cartService.addToCart("sessionId", 1L, 1);
        });

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    void testAddToCart_NewItem() {
        when(cartRepository.findBySessionId("sessionId")).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        cartService.addToCart("sessionId", 1L, 2);

        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(product, cart.getItems().get(0).getProduct());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testAddToCart_ExistingItem() {
        CartItem existingItem = new CartItem();
        existingItem.setProduct(product);
        existingItem.setQuantity(1);
        cart.getItems().add(existingItem);

        when(cartRepository.findBySessionId("sessionId")).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        cartService.addToCart("sessionId", 1L, 3);

        assertEquals(1, cart.getItems().size());
        assertEquals(4, cart.getItems().get(0).getQuantity());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testCalculateTotalAmount() {
        Product product1 = new Product();
        product1.setPrice(10.0);
        CartItem item1 = new CartItem();
        item1.setProduct(product1);
        item1.setQuantity(2);

        Product product2 = new Product();
        product2.setPrice(5.0);
        CartItem item2 = new CartItem();
        item2.setProduct(product2);
        item2.setQuantity(3);

        List<CartItem> items = List.of(item1, item2);
        double total = cartService.calculateTotalAmount(items);

        assertEquals(35.0, total);
    }

    @Test
    void testGetCartItems() {
        when(cartRepository.findBySessionId("sessionId")).thenReturn(Optional.of(cart));

        List<CartItem> result = cartService.getCartItems("sessionId");

        assertEquals(cart.getItems(), result);
        verify(cartRepository, times(1)).findBySessionId("sessionId");
    }

    @Test
    void testUpdateProductQuantity_Increase() {
        CartItem existingItem = new CartItem();
        existingItem.setProduct(product);
        existingItem.setQuantity(2);
        cart.getItems().add(existingItem);

        when(cartRepository.findBySessionId("sessionId")).thenReturn(Optional.of(cart));

        cartService.updateProductQuantity("sessionId", 1L, 3);

        assertEquals(5, existingItem.getQuantity());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testUpdateProductQuantity_DecreaseToZero() {
        CartItem existingItem = new CartItem();
        existingItem.setProduct(product);
        existingItem.setQuantity(1);
        cart.getItems().add(existingItem);

        when(cartRepository.findBySessionId("sessionId")).thenReturn(Optional.of(cart));

        cartService.updateProductQuantity("sessionId", 1L, -1);

        assertEquals(0, cart.getItems().size());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testRemoveProduct() {
        CartItem existingItem = new CartItem();
        existingItem.setProduct(product);
        cart.getItems().add(existingItem);

        when(cartRepository.findBySessionId("sessionId")).thenReturn(Optional.of(cart));

        cartService.removeProduct("sessionId", 1L);

        assertEquals(0, cart.getItems().size());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testDecreaseProductQuantity() {
        CartItem existingItem = new CartItem();
        existingItem.setProduct(product);
        existingItem.setQuantity(2);
        cart.getItems().add(existingItem);

        when(cartRepository.findBySessionId("sessionId")).thenReturn(Optional.of(cart));

        cartService.decreaseProductQuantity("sessionId", 1L);

        assertEquals(1, existingItem.getQuantity());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testDecreaseProductQuantity_RemoveItem() {
        CartItem existingItem = new CartItem();
        existingItem.setProduct(product);
        existingItem.setQuantity(1);
        cart.getItems().add(existingItem);

        when(cartRepository.findBySessionId("sessionId")).thenReturn(Optional.of(cart));

        cartService.decreaseProductQuantity("sessionId", 1L);

        assertEquals(0, cart.getItems().size());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testIncreaseProductQuantity() {
        CartItem existingItem = new CartItem();
        existingItem.setProduct(product);
        existingItem.setQuantity(1);
        cart.getItems().add(existingItem);

        when(cartRepository.findBySessionId("sessionId")).thenReturn(Optional.of(cart));

        cartService.increaseProductQuantity("sessionId", 1L);

        assertEquals(2, existingItem.getQuantity());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testClearCart() {
        cart.getItems().add(new CartItem());
        cart.getItems().add(new CartItem());

        when(cartRepository.findBySessionId("sessionId")).thenReturn(Optional.of(cart));

        cartService.clearCart("sessionId");

        assertEquals(0, cart.getItems().size());
        verify(cartRepository, times(1)).save(cart);
    }
}
