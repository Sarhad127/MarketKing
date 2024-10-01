package org.sarhad.marketking.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.sarhad.marketking.model.CartItem;
import org.sarhad.marketking.service.CartService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public String addToCart(@RequestParam("id") Long productId,
                            HttpSession session,
                            @RequestParam("quantity") int quantity) {
        String sessionId = session.getId();
        cartService.addToCart(sessionId, productId, quantity);
        return "redirect:/products";
    }

    @PostMapping("/addFromDetailPage")
    public String addToCart2(@RequestParam("id") Long productId,
                             HttpSession session,
                             @RequestParam("quantity") int quantity) {
        String sessionId = session.getId();
        cartService.addToCart(sessionId, productId, quantity);
        return "redirect:/products/" + productId;
    }

    @GetMapping
    public String viewCart(HttpSession session,
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        String sessionId = session.getId();
        List<CartItem> cartItems = cartService.getCartItems(sessionId);
        double totalAmount = cartService.calculateTotalAmount(cartItems);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("user", userDetails);
        return "cart";
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam Long id,
                             @RequestParam String action,
                             HttpSession session) {
        String sessionId = session.getId();
        if ("increase".equals(action)) {
            cartService.updateProductQuantity(sessionId, id, 1);
        } else if ("decrease".equals(action)) {
            cartService.updateProductQuantity(sessionId, id, -1);
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeProductFromCart(@RequestParam Long id,
                                        HttpSession session) {
        String sessionId = session.getId();
        cartService.removeProduct(sessionId, id);
        return "redirect:/cart";
    }

    @GetMapping("/messageSent")
    public String messageSent() {
        return "messageSent";
    }
}