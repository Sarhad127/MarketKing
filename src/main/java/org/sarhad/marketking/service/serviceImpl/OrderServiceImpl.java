package org.sarhad.marketking.service.serviceImpl;

import lombok.AllArgsConstructor;
import org.sarhad.marketking.model.*;
import org.sarhad.marketking.repository.OrderRepository;
import org.sarhad.marketking.service.OrderService;
import org.sarhad.marketking.service.ProductService;
import org.sarhad.marketking.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ProductService productService;

    @Override
    public List<Orders> getOrdersByUserId(Long userId) {
        Users user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findByUser(user);
    }

    @Override
    public Orders saveOrder(Orders order) {
        return orderRepository.save(order);
    }

    @Override
    public Orders createOrder(List<CartItem> cartItems, Long userId) {
        Users user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Orders order = new Orders();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Product product = productService.getProductById(cartItem.getProduct().getId());
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        return orderRepository.save(order);
    }
}
