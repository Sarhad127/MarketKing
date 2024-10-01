package org.sarhad.marketking.service;

import org.sarhad.marketking.model.CartItem;
import org.sarhad.marketking.model.Orders;

import java.util.List;

public interface OrderService {
    List<Orders> getOrdersByUserId(Long userId);
    Orders saveOrder(Orders order);
    Orders createOrder(List<CartItem> items, Long userId);
}
