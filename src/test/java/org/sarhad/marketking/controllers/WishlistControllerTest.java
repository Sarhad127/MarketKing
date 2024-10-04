package org.sarhad.marketking.controllers;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sarhad.marketking.model.Users;
import org.sarhad.marketking.model.Wishlist;
import org.sarhad.marketking.security.CustomUserDetails;
import org.sarhad.marketking.service.UserService;
import org.sarhad.marketking.service.WishesService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.Model;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ActiveProfiles("test")
class WishlistControllerTest {

    @Mock
    private WishesService wishesService;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private CustomUserDetails customUserDetails;

    @Mock
    private Principal principal;

    @Mock
    private Users user;

    @InjectMocks
    private WishlistController wishlistController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new Users();
        user.setId(1L);
        user.setUsername("TestUser");
    }

    @Test
    void getWishlist_Success() {
        List<Wishlist> wishlistItems = Collections.singletonList(new Wishlist(user, null));
        when(wishesService.getWishlistByUserId(1L)).thenReturn(wishlistItems);
        when(userService.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(customUserDetails.getUserId()).thenReturn(1L);

        String viewName = wishlistController.getWishlist(customUserDetails, model);

        assertEquals("wishlist", viewName);
        verify(model).addAttribute("wishlistItems", wishlistItems);
        verify(model).addAttribute("user", customUserDetails);
    }

    @Test
    void addToWishlist_Success() {
        Long productId = 1L;
        when(customUserDetails.getUserId()).thenReturn(1L);

        String viewName = wishlistController.addToWishlist(productId, customUserDetails);

        assertEquals("redirect:/wishlist", viewName);
        verify(wishesService).addProductToWishlist(productId, 1L);
    }

    @Test
    void removeFromWishlist_Success() {
        Long productId = 1L;
        when(principal.getName()).thenReturn("TestUser");

        String viewName = wishlistController.removeFromWishlist(productId, principal);

        assertEquals("redirect:/wishlist", viewName);
        verify(wishesService).removeProductFromWishlist(productId, "TestUser");
    }

    @Test
    void isProductInWishlist_Success() {
        Long productId = 1L;
        when(principal.getName()).thenReturn("TestUser");
        when(wishesService.isProductInWishlist(productId, "TestUser")).thenReturn(true);

        boolean exists = wishlistController.isProductInWishlist(productId, principal);

        assertTrue(exists);
        verify(wishesService).isProductInWishlist(productId, "TestUser");
    }
}
