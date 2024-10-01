package org.sarhad.marketking.service.serviceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sarhad.marketking.model.Product;
import org.sarhad.marketking.model.Users;
import org.sarhad.marketking.model.Wishlist;
import org.sarhad.marketking.repository.WishlistRepository;
import org.sarhad.marketking.service.ProductService;
import org.sarhad.marketking.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WishesServiceImplTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private WishesServiceImpl wishesService;

    private Users user;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new Users();
        user.setId(1L);
        user.setUsername("testuser");

        product = new Product();
        product.setId(1L);
        product.setTitle("Test Product");
    }

    @Test
    void testAddProductToWishlist_UserAndProductExist_AddedToWishlist() {
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(productService.getProductById(1L)).thenReturn(product);
        when(wishlistRepository.existsByUserAndProduct(user, product)).thenReturn(false);

        wishesService.addProductToWishlist(1L, 1L);

        verify(wishlistRepository, times(1)).save(any(Wishlist.class));
        verify(wishlistRepository, times(1)).existsByUserAndProduct(user, product);
        System.out.println("Product added to wishlist: " + product.getId());
    }

    @Test
    void testAddProductToWishlist_ProductAlreadyInWishlist() {
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(productService.getProductById(1L)).thenReturn(product);
        when(wishlistRepository.existsByUserAndProduct(user, product)).thenReturn(true);

        wishesService.addProductToWishlist(1L, 1L);

        verify(wishlistRepository, never()).save(any(Wishlist.class));
        verify(wishlistRepository, times(1)).existsByUserAndProduct(user, product);
        System.out.println("Product already exists in the wishlist.");
    }

    @Test
    void testAddProductToWishlist_UserOrProductNotFound() {
        when(userService.findById(1L)).thenReturn(Optional.empty());

        wishesService.addProductToWishlist(1L, 1L);

        verify(wishlistRepository, never()).save(any(Wishlist.class));
        System.out.println("User or product not found.");
    }

    @Test
    void testGetWishlist_UserExists() {
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUser(user)).thenReturn(new ArrayList<>());

        List<Wishlist> result = wishesService.getWishlist("testuser");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userService, times(1)).findByUsername("testuser");
        verify(wishlistRepository, times(1)).findByUser(user);
    }

    @Test
    void testGetWishlist_UserDoesNotExist() {
        when(userService.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        List<Wishlist> result = wishesService.getWishlist("nonexistentuser");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userService, times(1)).findByUsername("nonexistentuser");
        verify(wishlistRepository, never()).findByUser(any());
    }

    @Test
    void testGetWishlistByUserId_UserExists() {
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUser(user)).thenReturn(new ArrayList<>());

        List<Wishlist> result = wishesService.getWishlistByUserId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userService, times(1)).findById(1L);
        verify(wishlistRepository, times(1)).findByUser(user);
    }

    @Test
    void testGetWishlistByUserId_UserDoesNotExist() {
        when(userService.findById(2L)).thenReturn(Optional.empty());

        List<Wishlist> result = wishesService.getWishlistByUserId(2L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userService, times(1)).findById(2L);
        verify(wishlistRepository, never()).findByUser(any());
    }

    @Test
    void testRemoveProductFromWishlist_UserAndProductExist() {
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(productService.getProductById(1L)).thenReturn(product);

        wishesService.removeProductFromWishlist(1L, "testuser");

        verify(wishlistRepository, times(1)).deleteByUserAndProduct(user, product);
    }

    @Test
    void testRemoveProductFromWishlist_UserOrProductDoesNotExist() {
        when(userService.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        wishesService.removeProductFromWishlist(1L, "nonexistentuser");

        verify(wishlistRepository, never()).deleteByUserAndProduct(any(), any());
    }

    @Test
    void testIsProductInWishlist_ProductInWishlist() {
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(productService.getProductById(1L)).thenReturn(product);
        when(wishlistRepository.existsByUserAndProduct(user, product)).thenReturn(true);

        boolean result = wishesService.isProductInWishlist(1L, "testuser");

        assertTrue(result);
        verify(userService, times(1)).findByUsername("testuser");
        verify(productService, times(1)).getProductById(1L);
        verify(wishlistRepository, times(1)).existsByUserAndProduct(user, product);
    }

    @Test
    void testIsProductInWishlist_ProductNotInWishlist() {
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(productService.getProductById(1L)).thenReturn(product);
        when(wishlistRepository.existsByUserAndProduct(user, product)).thenReturn(false);

        boolean result = wishesService.isProductInWishlist(1L, "testuser");

        assertFalse(result);
        verify(userService, times(1)).findByUsername("testuser");
        verify(productService, times(1)).getProductById(1L);
        verify(wishlistRepository, times(1)).existsByUserAndProduct(user, product);
    }

    @Test
    void testIsProductInWishlist_UserOrProductDoesNotExist() {
        when(userService.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        boolean result = wishesService.isProductInWishlist(1L, "nonexistentuser");

        assertFalse(result);
        verify(userService, times(1)).findByUsername("nonexistentuser");
        verify(productService, never()).getProductById(anyLong());
        verify(wishlistRepository, never()).existsByUserAndProduct(any(), any());
    }

}
