package org.sarhad.marketking.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sarhad.marketking.model.Orders;
import org.sarhad.marketking.security.CustomUserDetails;
import org.sarhad.marketking.service.OrderService;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderHistoryControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private Model model;

    @Mock
    private CustomUserDetails customUserDetails;

    @InjectMocks
    private OrderHistoryController orderHistoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void viewOrderHistory() {
        Long userId = 1L;
        when(customUserDetails.getUserId()).thenReturn(userId);

        List<Orders> orders = new ArrayList<>();
        Orders order = new Orders();
        orders.add(order);

        when(orderService.getOrdersByUserId(userId)).thenReturn(orders);

        String viewName = orderHistoryController.viewOrderHistory(customUserDetails, model);

        verify(model).addAttribute("orders", orders);
        verify(model).addAttribute("customUserDetails", customUserDetails);
        assertEquals("order-history", viewName);
    }
}
