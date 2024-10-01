package org.sarhad.marketking.controllers;

import lombok.AllArgsConstructor;
import org.sarhad.marketking.model.Product;
import org.sarhad.marketking.model.Review;
import org.sarhad.marketking.repository.ProductRepository;
import org.sarhad.marketking.repository.ReviewRepository;
import org.sarhad.marketking.service.ProductService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    private final ProductRepository productRepository;

    private final ReviewRepository reviewRepository;

    @GetMapping("/about")
    public String getAbout(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("user", userDetails);
        return "about";
    }

    @GetMapping("/contact")
    public String getContact(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("user", userDetails);
        return "contact";
    }
    @PostMapping("/contact")
    public String sendMessage() {
        return "redirect:/cart/messageSent";
    }

    @GetMapping
    public String getAllProducts(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        List<Product> products = productService.getAllProducts();
        if (!search.isEmpty()) {
            products = products.stream()
                    .filter(product -> product.getTitle().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        }
        int pageSize = 6;
        int totalProducts = products.size();
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);

        if (page < 0) {
            page = 0;
        } else if (page >= totalPages && totalPages > 0) {
            page = totalPages - 1;
        }
        if (totalProducts == 0) {
            model.addAttribute("products", Collections.emptyList());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
        } else {
            int start = page * pageSize;
            int end = Math.min(start + pageSize, totalProducts);
            List<Product> paginatedProducts = products.subList(start, end);
            model.addAttribute("products", paginatedProducts);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
        }

        model.addAttribute("search", search);
        model.addAttribute("user", userDetails);
        System.out.println("Current User: " + userDetails);
        return "products";
    }

    @GetMapping("/{id}")
    public String getProductDetail(@PathVariable Long id,
                                   @RequestParam(required = false, defaultValue = "") String search,
                                   @RequestParam(defaultValue = "0") int page,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        Product product = productService.getProductById(id);

        if (product == null) {
            return "error";
        }
        model.addAttribute("product", product);
        model.addAttribute("search", search);
        model.addAttribute("currentPage", page);
        model.addAttribute("user", userDetails);
        return "product-detail";
    }

    @PostMapping("/{id}/reviews")
    public String addReview(@PathVariable Long id,
                            @RequestParam String comment,
                            @RequestParam int rating) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        String reviewerName = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            reviewerName = userDetails.getUsername();
        }

        Review review = new Review();
        review.setComment(comment);
        review.setRating(rating);
        review.setReviewerName(reviewerName);
        review.setProduct(product);

        reviewRepository.save(review);

        return "redirect:/products/" + id;
    }
}