package org.sarhad.marketking;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sarhad.marketking.controllers.CartController;
import org.sarhad.marketking.service.CartService;
import org.springframework.ui.Model;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CartControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private CartController cartController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addToCart() {
        when(session.getId()).thenReturn("sessionId");

        String result = cartController.addToCart(1L, session, 2);

        verify(cartService).addToCart("sessionId", 1L, 2);
        assertEquals("redirect:/products", result);
    }

    @Test
    void addToCart2() {
        when(session.getId()).thenReturn("sessionId");

        String result = cartController.addToCart2(1L, session, 2);

        verify(cartService).addToCart("sessionId", 1L, 2);
        assertEquals("redirect:/products/1", result);
    }

    @Test
    void viewCart() {
        when(session.getId()).thenReturn("sessionId");
        when(cartService.getCartItems("sessionId")).thenReturn(Collections.emptyList());
        when(cartService.calculateTotalAmount(any())).thenReturn(0.0);

        String result = cartController.viewCart(session, null, model);

        verify(cartService).getCartItems("sessionId");
        verify(cartService).calculateTotalAmount(Collections.emptyList());
        assertEquals("cart", result);
        verify(model).addAttribute("cartItems", Collections.emptyList());
        verify(model).addAttribute("totalAmount", 0.0);
        verify(model).addAttribute("user", null);
    }

    @Test
    void updateCart() {
        when(session.getId()).thenReturn("sessionId");

        String result = cartController.updateCart(1L, "increase", session);

        verify(cartService).updateProductQuantity("sessionId", 1L, 1);
        assertEquals("redirect:/cart", result);
    }

    @Test
    void removeProductFromCart() {
        when(session.getId()).thenReturn("sessionId");

        String result = cartController.removeProductFromCart(1L, session);

        verify(cartService).removeProduct("sessionId", 1L);
        assertEquals("redirect:/cart", result);
    }

    @Test
    void messageSent() {
        String result = cartController.messageSent();
        assertEquals("messageSent", result);
    }
}
