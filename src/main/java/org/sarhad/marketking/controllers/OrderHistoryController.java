package org.sarhad.marketking.controllers;

import lombok.AllArgsConstructor;
import org.sarhad.marketking.model.Orders;
import org.sarhad.marketking.security.CustomUserDetails;
import org.sarhad.marketking.service.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class OrderHistoryController {

    private final OrderService orderService;

    @GetMapping("/order-history")
    public String viewOrderHistory(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                   Model model) {
        Long userId = customUserDetails.getUserId();
        List<Orders> orders = orderService.getOrdersByUserId(userId);
        model.addAttribute("orders", orders);
        model.addAttribute("customUserDetails", customUserDetails);
        return "order-history";
    }
}
