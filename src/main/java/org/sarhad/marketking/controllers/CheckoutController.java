package org.sarhad.marketking.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.sarhad.marketking.model.CartItem;
import org.sarhad.marketking.model.Users;
import org.sarhad.marketking.security.CustomUserDetails;
import org.sarhad.marketking.service.CartService;
import org.sarhad.marketking.service.OrderService;
import org.sarhad.marketking.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
public class CheckoutController {

    private final CartService cartService;
    private final UserService userService;
    private final OrderService orderService;

    @GetMapping("/checkout")
    public String showCheckout(HttpSession session,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               Model model) {
        String sessionId = session.getId();
        List<CartItem> cartItems = cartService.getCartItems(sessionId);
        model.addAttribute("cartItems", cartItems);
        double totalAmount = cartService.calculateTotalAmount(cartItems);
        model.addAttribute("totalAmount", totalAmount);
        if (userDetails != null) {
            Long userId = userDetails.getUserId();
            Users user = userService.findById(userId).orElse(null);
            if (user != null) {
                model.addAttribute("username", user.getUsername());
                model.addAttribute("email", user.getEmail());
                model.addAttribute("address", user.getAddress());
                model.addAttribute("phone", user.getPhone());
                model.addAttribute("user", user);
            }
        }
        return "checkout";
    }

    @PostMapping("/checkout/process")
    public String processCheckout(HttpSession session,
                                  @RequestParam String username,
                                  @RequestParam String password,
                                  @RequestParam String email,
                                  @RequestParam String address,
                                  @RequestParam String phone,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                  Model model) {
        String sessionId = session.getId();
        System.out.println("Session ID: " + sessionId);

        List<CartItem> cartItems = cartService.getCartItems(sessionId);
        double totalAmount = cartService.calculateTotalAmount(cartItems);

        if (cartItems.isEmpty()) {
            model.addAttribute("errorMessage", "Your cart is empty. Please add items to your cart before checking out.");
            model.addAttribute("customerName", username);
            model.addAttribute("email", email);
            model.addAttribute("address", address);
            model.addAttribute("phone", phone);
            model.addAttribute("user", userDetails);
            return "checkout";
        }

        if (customUserDetails != null) {
            Users user = userService.findById(customUserDetails.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!userService.checkPassword(user, password)) {
                model.addAttribute("errorMessage", "Incorrect password. Please try again.");
                model.addAttribute("cartItems", cartItems);
                model.addAttribute("totalAmount", totalAmount);
                model.addAttribute("customerName", username);
                model.addAttribute("email", email);
                model.addAttribute("address", address);
                model.addAttribute("phone", phone);
                model.addAttribute("user", userDetails);
                return "checkout";
            }
        }

        model.addAttribute("orderedItems", cartItems);
        model.addAttribute("customerName", username);
        model.addAttribute("customerEmail", email);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("user", userDetails);

        if (customUserDetails != null) {
            orderService.createOrder(cartItems, customUserDetails.getUserId());
        }
        cartService.clearCart(sessionId);
        return "thank_you";
    }
}