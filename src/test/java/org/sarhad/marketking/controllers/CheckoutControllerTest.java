package org.sarhad.marketking.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sarhad.marketking.model.CartItem;
import org.sarhad.marketking.model.Users;
import org.sarhad.marketking.security.CustomUserDetails;
import org.sarhad.marketking.service.CartService;
import org.sarhad.marketking.service.OrderService;
import org.sarhad.marketking.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CheckoutControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private UserService userService;

    @Mock
    private OrderService orderService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private UserDetails userDetails;

    @Mock
    private CustomUserDetails customUserDetails;

    @InjectMocks
    private CheckoutController checkoutController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void showCheckout() {
        String sessionId = "testSessionId";
        when(session.getId()).thenReturn(sessionId);

        List<CartItem> cartItems = new ArrayList<>();
        double totalAmount = 100.0;

        when(cartService.getCartItems(sessionId)).thenReturn(cartItems);
        when(cartService.calculateTotalAmount(cartItems)).thenReturn(totalAmount);

        Users user = new Users();
        user.setUsername("testUser");
        user.setEmail("email@example.com");
        user.setAddress("Test Address");
        user.setPhone("1234567890");
        when(customUserDetails.getUserId()).thenReturn(1L);
        when(userService.findById(1L)).thenReturn(Optional.of(user));

        String viewName = checkoutController.showCheckout(session, customUserDetails, model);

        verify(cartService).getCartItems(sessionId);
        verify(cartService).calculateTotalAmount(cartItems);
        verify(model).addAttribute("cartItems", cartItems);
        verify(model).addAttribute("totalAmount", totalAmount);
        verify(model).addAttribute("username", "testUser");
        verify(model).addAttribute("email", "email@example.com");
        verify(model).addAttribute("address", "Test Address");
        verify(model).addAttribute("phone", "1234567890");
        verify(model).addAttribute("user", user);

        assertEquals("checkout", viewName);
    }

    @Test
    void processCheckout_withItems_noCustomUserDetails() {
        String sessionId = "testSessionId";
        when(session.getId()).thenReturn(sessionId);

        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(new CartItem());

        when(cartService.getCartItems(sessionId)).thenReturn(cartItems);
        when(cartService.calculateTotalAmount(cartItems)).thenReturn(200.0);

        String viewName = checkoutController.processCheckout(session, "testUser", "password",
                "email@example.com", "Test Address", "1234567890", userDetails, null, model);

        verify(cartService).clearCart(sessionId);
        verify(cartService).calculateTotalAmount(cartItems);
        assertEquals("thank_you", viewName);
    }

    @Test
    void processCheckout_withIncorrectPassword() {
        String sessionId = "testSessionId";
        when(session.getId()).thenReturn(sessionId);

        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(new CartItem());

        Users user = new Users();
        user.setUsername("testUser");
        when(customUserDetails.getUserId()).thenReturn(1L);
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(userService.checkPassword(user, "wrongPassword")).thenReturn(false);

        when(cartService.getCartItems(sessionId)).thenReturn(cartItems);
        when(cartService.calculateTotalAmount(cartItems)).thenReturn(200.0);

        String viewName = checkoutController.processCheckout(session, "testUser", "wrongPassword",
                "email@example.com", "Test Address", "1234567890", userDetails, customUserDetails, model);

        verify(model).addAttribute("errorMessage", "Incorrect password. Please try again.");
        verify(model).addAttribute("cartItems", cartItems);
        verify(model).addAttribute("totalAmount", 200.0);
        assertEquals("checkout", viewName);
    }

    @Test
    void processCheckout_emptyCart() {
        String sessionId = "testSessionId";
        when(session.getId()).thenReturn(sessionId);

        when(cartService.getCartItems(sessionId)).thenReturn(new ArrayList<>());

        String viewName = checkoutController.processCheckout(session, "testUser", "password",
                "email@example.com", "Test Address", "1234567890", userDetails, null, model);

        verify(model).addAttribute("errorMessage", "Your cart is empty. Please add items to your cart before checking out.");
        assertEquals("checkout", viewName);
    }

    @Test
    void processCheckout_withCustomUserDetailsAndItems() {
        String sessionId = "testSessionId";
        when(session.getId()).thenReturn(sessionId);

        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(new CartItem());

        Users user = new Users();
        user.setUsername("testUser");
        user.setPassword("encodedPassword");
        when(customUserDetails.getUserId()).thenReturn(1L);
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(userService.checkPassword(user, "password")).thenReturn(true);

        when(cartService.getCartItems(sessionId)).thenReturn(cartItems);
        when(cartService.calculateTotalAmount(cartItems)).thenReturn(200.0);

        String viewName = checkoutController.processCheckout(session, "testUser", "password",
                "email@example.com", "Test Address", "1234567890", userDetails, customUserDetails, model);

        verify(cartService).clearCart(sessionId);
        verify(orderService).createOrder(cartItems, 1L);
        assertEquals("thank_you", viewName);
    }
}
