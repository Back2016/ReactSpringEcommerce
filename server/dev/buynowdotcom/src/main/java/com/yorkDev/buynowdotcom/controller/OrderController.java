package com.yorkDev.buynowdotcom.controller;

import com.yorkDev.buynowdotcom.dtos.OrderDto;
import com.yorkDev.buynowdotcom.model.Order;
import com.yorkDev.buynowdotcom.response.ApiResponse;
import com.yorkDev.buynowdotcom.service.order.IOrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/orders")
public class OrderController {
    private final IOrderService orderService;

    @PostMapping("/user/placeOrder")
    public ResponseEntity<ApiResponse> placeOrder(@RequestParam Long userId) {
        OrderDto orderDto = orderService.placeOrder(userId);
        return ResponseEntity.ok(new ApiResponse("Order placed!", orderDto));
    }

    @GetMapping("/user/{userId}")
    private ResponseEntity<ApiResponse> getUserOrders(@PathVariable Long userId) {
        List<OrderDto> orders = orderService.getUserOrders(userId);

        return ResponseEntity.ok(new ApiResponse("Success!", orders));
    }
}
