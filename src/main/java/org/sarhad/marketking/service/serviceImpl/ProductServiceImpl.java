package org.sarhad.marketking.service.serviceImpl;

import lombok.AllArgsConstructor;
import org.sarhad.marketking.model.*;
import org.sarhad.marketking.repository.ProductRepository;
import org.sarhad.marketking.repository.ReviewRepository;
import org.sarhad.marketking.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {


    private final ProductRepository productRepository;

    private final ReviewRepository reviewRepository;

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void fetchAndStoreProducts() {
        String apiUrl = "https://dummyjson.com/products";
        Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);

        List<Map<String, Object>> products = (List<Map<String, Object>>) response.get("products");

        for (Map<String, Object> productData : products) {
            try {
                Product product = new Product();
                product.setId(Long.valueOf((Integer) productData.get("id")));
                product.setTitle((String) productData.get("title"));
                product.setDescription((String) productData.get("description"));
                product.setCategory((String) productData.get("category"));
                product.setPrice(((Number) productData.get("price")).doubleValue());
                product.setDiscountPercentage(((Number) productData.get("discountPercentage")).doubleValue());
                product.setRating(((Number) productData.get("rating")).doubleValue());
                product.setStock((Integer) productData.get("stock"));
                product.setBrand((String) productData.get("brand"));
                product.setSku((String) productData.get("sku"));
                product.setWeight(((Number) productData.get("weight")).doubleValue());
                product.setWarrantyInformation((String) productData.get("warrantyInformation"));
                product.setShippingInformation((String) productData.get("shippingInformation"));
                product.setAvailabilityStatus((String) productData.get("availabilityStatus"));
                product.setReturnPolicy((String) productData.get("returnPolicy"));
                product.setMinimumOrderQuantity((Integer) productData.get("minimumOrderQuantity"));
                product.setThumbnail((String) productData.get("thumbnail"));

                Map<String, Object> dimensionsMap = (Map<String, Object>) productData.get("dimensions");
                if (dimensionsMap != null) {
                    Dimensions dimensions = new Dimensions();
                    dimensions.setWidth(((Number) dimensionsMap.get("width")).doubleValue());
                    dimensions.setHeight(((Number) dimensionsMap.get("height")).doubleValue());
                    dimensions.setDepth(((Number) dimensionsMap.get("depth")).doubleValue());
                    product.setDimensions(dimensions);
                }

                Map<String, Object> metaMap = (Map<String, Object>) productData.get("meta");
                if (metaMap != null) {
                    MetaData metaData = new MetaData();
                    metaData.setCreatedAt((String) metaMap.get("createdAt"));
                    metaData.setUpdatedAt((String) metaMap.get("updatedAt"));
                    metaData.setBarcode((String) metaMap.get("barcode"));
                    metaData.setQrCode((String) metaMap.get("qrCode"));
                    product.setMeta(metaData);
                }

                List<String> imagesList = (List<String>) productData.get("images");
                if (imagesList != null) {
                    for (String imageUrl : imagesList) {
                        Images image = new Images();
                        image.setImageUrl(imageUrl);
                        image.setProduct(product);
                        product.getImages().add(image);
                    }
                }
                List<Map<String, Object>> reviewsList = (List<Map<String, Object>>) productData.get("reviews");
                if (reviewsList != null) {
                    for (Map<String, Object> reviewData : reviewsList) {
                        Review review = new Review();
                        review.setRating((Integer) reviewData.get("rating"));
                        review.setComment((String) reviewData.get("comment"));
                        review.setReviewerEmail((String) reviewData.get("reviewerEmail"));
                        review.setReviewerName((String) reviewData.get("reviewerName"));
                        review.setDate(java.sql.Timestamp.valueOf(((String) reviewData.get("date")).replace("T", " ").replace("Z", "")));
                        review.setProduct(product);
                        product.getReviews().add(review);
                    }
                }
                productRepository.save(product);
            } catch (Exception e) {
                logger.error("Failed to save product: {}", productData, e);
            }
        }
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public void addReview(Long productId, Review review) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        review.setProduct(product);
        reviewRepository.save(review);
    }

    @Override
    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
}

