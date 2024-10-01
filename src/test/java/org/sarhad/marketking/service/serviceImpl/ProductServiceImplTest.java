package org.sarhad.marketking.service.serviceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sarhad.marketking.model.*;
import org.sarhad.marketking.repository.ProductRepository;
import org.sarhad.marketking.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private List<Product> productList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        product = new Product();
        product.setId(1L);
        product.setTitle("Sample Product");
        productList = new ArrayList<>();
        productList.add(product);
    }

    @Test
    void fetchAndStoreProducts_Success() {
        List<Map<String, Object>> mockResponse = new ArrayList<>();
        Map<String, Object> productData = new HashMap<>();
        productData.put("id", 1);
        productData.put("title", "Sample Product");
        productData.put("description", "Product Description");
        productData.put("category", "Sample Category");
        productData.put("price", 10.0);
        productData.put("discountPercentage", 5.0);
        productData.put("rating", 4.5);
        productData.put("stock", 100);
        productData.put("brand", "Sample Brand");
        productData.put("sku", "SKU123");
        productData.put("weight", 1.5);
        productData.put("warrantyInformation", "1 Year");
        productData.put("shippingInformation", "Free Shipping");
        productData.put("availabilityStatus", "In Stock");
        productData.put("returnPolicy", "30 Days");
        productData.put("minimumOrderQuantity", 1);
        productData.put("thumbnail", "http://example.com/thumbnail.jpg");
        productData.put("images", List.of("http://example.com/image1.jpg", "http://example.com/image2.jpg"));

        Map<String, Object> dimensions = new HashMap<>();
        dimensions.put("width", 5.0);
        dimensions.put("height", 10.0);
        dimensions.put("depth", 2.0);
        productData.put("dimensions", dimensions);

        Map<String, Object> metaData = new HashMap<>();
        metaData.put("createdAt", "2023-09-30T10:00:00Z");
        metaData.put("updatedAt", "2023-09-30T10:00:00Z");
        metaData.put("barcode", "123456789");
        metaData.put("qrCode", "qrCodeData");
        productData.put("meta", metaData);

        List<Map<String, Object>> reviews = new ArrayList<>();
        Map<String, Object> reviewData = new HashMap<>();
        reviewData.put("rating", 5);
        reviewData.put("comment", "Great product!");
        reviewData.put("reviewerEmail", "reviewer@example.com");
        reviewData.put("reviewerName", "John Doe");
        reviewData.put("date", "2023-09-30T10:00:00Z");
        reviews.add(reviewData);
        productData.put("reviews", reviews);

        mockResponse.add(productData);

        Map<String, Object> apiResponse = new HashMap<>();
        apiResponse.put("products", mockResponse);

        when(restTemplate.getForObject("https://dummyjson.com/products", Map.class)).thenReturn(apiResponse);
        productService.fetchAndStoreProducts();
        verify(productRepository, atLeastOnce()).save(any(Product.class));
    }

    @Test
    void getAllProducts_ReturnsProductList() {
        when(productRepository.findAll()).thenReturn(productList);

        List<Product> result = productService.getAllProducts();

        assertEquals(1, result.size());
        assertEquals(product, result.get(0));
    }

    @Test
    void getProductById_Exists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(product, result);
    }

    @Test
    void getProductById_NotExists() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        Product result = productService.getProductById(2L);

        assertNull(result);
    }

    @Test
    void addReview_ProductExists() {
        Review review = new Review();
        review.setRating(5);
        review.setComment("Great product!");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.addReview(1L, review);

        verify(reviewRepository).save(review);
        assertEquals(product, review.getProduct());
    }

    @Test
    void addReview_ProductNotFound() {
        Review review = new Review();

        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.addReview(2L, review);
        });

        assertEquals("Product not found", exception.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void getProducts_ReturnsPagedProducts() {
        Pageable pageable = mock(Pageable.class);
        Page<Product> productPage = mock(Page.class);
        when(productRepository.findAll(pageable)).thenReturn(productPage);

        Page<Product> result = productService.getProducts(pageable);

        assertEquals(productPage, result);
    }
}
