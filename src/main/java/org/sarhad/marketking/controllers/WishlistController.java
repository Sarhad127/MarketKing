package org.sarhad.marketking.controllers;

import lombok.AllArgsConstructor;
import org.sarhad.marketking.model.Wishlist;
import org.sarhad.marketking.security.CustomUserDetails;
import org.sarhad.marketking.service.WishesService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/wishlist")
public class WishlistController {

    private final WishesService wishesService;

    @GetMapping
    public String getWishlist(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                              Model model) {
        List<Wishlist> wishlistItems = wishesService.getWishlistByUserId(customUserDetails.getUserId());
        model.addAttribute("wishlistItems", wishlistItems);
        model.addAttribute("user", customUserDetails);
        return "wishlist";
    }

    @PostMapping("/add")
    public String addToWishlist(@RequestParam Long productId,
                                @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        wishesService.addProductToWishlist(productId, customUserDetails.getUserId());
        return "redirect:/wishlist";
    }

    @PostMapping("/remove")
    public String removeFromWishlist(@RequestParam Long productId, Principal principal) {
        wishesService.removeProductFromWishlist(productId, principal.getName());
        return "redirect:/wishlist";
    }

    @GetMapping("/exists")
    public boolean isProductInWishlist(@RequestParam Long productId, Principal principal) {
        return wishesService.isProductInWishlist(productId, principal.getName());
    }
}
