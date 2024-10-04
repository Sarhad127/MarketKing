package org.sarhad.marketking.service.serviceImpl;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sarhad.marketking.model.*;
import org.sarhad.marketking.repository.OrderRepository;
import org.sarhad.marketking.service.ProductService;
import org.sarhad.marketking.service.UserService;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ActiveProfiles("test")
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Users user;
    private Orders order;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new Users();
        user.setId(1L);

        order = new Orders();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderItems(new ArrayList<>());

        cartItem = new CartItem();
        Product product = new Product();
        product.setId(1L);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
    }

    @Test
    void testGetOrdersByUserId_WhenUserExists() {
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(orderRepository.findByUser(user)).thenReturn(List.of(order));

        List<Orders> orders = orderService.getOrdersByUserId(1L);

        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals(order, orders.get(0));
        verify(userService, times(1)).findById(1L);
        verify(orderRepository, times(1)).findByUser(user);
    }

    @Test
    void testGetOrdersByUserId_WhenUserDoesNotExist() {
        when(userService.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrdersByUserId(1L);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userService, times(1)).findById(1L);
        verify(orderRepository, never()).findByUser(any());
    }

    @Test
    void testSaveOrder() {
        when(orderRepository.save(order)).thenReturn(order);

        Orders savedOrder = orderService.saveOrder(order);

        assertNotNull(savedOrder);
        assertEquals(order, savedOrder);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testCreateOrder() {
        List<CartItem> cartItems = List.of(cartItem);
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(productService.getProductById(cartItem.getProduct().getId())).thenReturn(cartItem.getProduct());
        when(orderRepository.save(any(Orders.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Orders createdOrder = orderService.createOrder(cartItems, 1L);

        assertNotNull(createdOrder);
        assertEquals(user, createdOrder.getUser());
        assertEquals(cartItems.size(), createdOrder.getOrderItems().size());
        assertEquals(cartItem.getQuantity(), createdOrder.getOrderItems().get(0).getQuantity());
        verify(userService, times(1)).findById(1L);
        verify(productService, times(1)).getProductById(cartItem.getProduct().getId());
        verify(orderRepository, times(1)).save(any(Orders.class));
    }

    @Test
    void testCreateOrder_WhenUserDoesNotExist() {
        List<CartItem> cartItems = List.of(cartItem);
        when(userService.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(cartItems, 1L);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userService, times(1)).findById(1L);
        verify(productService, never()).getProductById(any());
        verify(orderRepository, never()).save(any());
    }
}
