package org.sarhad.marketking.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.sarhad.marketking.model.Product;
import org.sarhad.marketking.model.Review;
import org.sarhad.marketking.repository.ProductRepository;
import org.sarhad.marketking.repository.ReviewRepository;
import org.sarhad.marketking.service.ProductService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ActiveProfiles("test")
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private Model model;

    @Mock
    private UserDetails userDetails;

    @Mock
    private Product product;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        product = new Product();
        product.setId(1L);
        product.setTitle("Test Product");
        product.setDescription("This is a test product.");
    }

    @Test
    void getAbout() {
        String viewName = productController.getAbout(model, userDetails);
        assertEquals("about", viewName);
        verify(model).addAttribute("user", userDetails);
    }

    @Test
    void getContact() {
        String viewName = productController.getContact(model, userDetails);
        assertEquals("contact", viewName);
        verify(model).addAttribute("user", userDetails);
    }

    @Test
    void sendMessage() {
        String viewName = productController.sendMessage();
        assertEquals("redirect:/cart/messageSent", viewName);
    }

    @Test
    void getAllProducts_NoSearch_NoProducts() {
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());

        String viewName = productController.getAllProducts("", 0, userDetails, model);

        assertEquals("products", viewName);
        verify(model).addAttribute("products", Collections.emptyList());
        verify(model).addAttribute("currentPage", 0);
        verify(model).addAttribute("totalPages", 0);
    }

    @Test
    void getAllProducts_WithSearch() {
        when(productService.getAllProducts()).thenReturn(List.of(product));

        String viewName = productController.getAllProducts("Test", 0, userDetails, model);

        assertEquals("products", viewName);
        verify(model).addAttribute("products", List.of(product));
        verify(model).addAttribute("currentPage", 0);
        verify(model).addAttribute("totalPages", 1);
    }

    @Test
    void getProductDetail_ProductFound() {
        when(productService.getProductById(1L)).thenReturn(product);

        String viewName = productController.getProductDetail(1L, "", 0, userDetails, model);

        assertEquals("product-detail", viewName);
        verify(model).addAttribute("product", product);
        verify(model).addAttribute("search", "");
        verify(model).addAttribute("currentPage", 0);
    }

    @Test
    void getProductDetail_ProductNotFound() {
        when(productService.getProductById(1L)).thenReturn(null);

        String viewName = productController.getProductDetail(1L, "", 0, userDetails, model);

        assertEquals("error", viewName);
    }

    @Test
    void addReview_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userDetails.getUsername()).thenReturn("TestUser");
        String viewName = productController.addReview(1L, "Great product!", 5);
        assertEquals("redirect:/products/1", viewName);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void addReview_ProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productController.addReview(1L, "Great product!", 5);
        });

        assertEquals("Product not found", exception.getMessage());
    }
}
