package com.yorkDev.buynowdotcom.controller;

import com.yorkDev.buynowdotcom.dtos.OrderDto;
import com.yorkDev.buynowdotcom.model.Order;
import com.yorkDev.buynowdotcom.model.User;
import com.yorkDev.buynowdotcom.request.PlaceOrderRequest;
import com.yorkDev.buynowdotcom.response.ApiResponse;
import com.yorkDev.buynowdotcom.service.order.IOrderService;
import com.yorkDev.buynowdotcom.service.user.IUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/orders")
public class OrderController {
    private final IOrderService orderService;
    private final IUserService userService;

    @PostMapping("/user/placeOrder")
    public ResponseEntity<ApiResponse> placeOrder(@RequestBody PlaceOrderRequest request) {
        User user = userService.getAuthenticatedUser();
        OrderDto orderDto = orderService.placeOrder(user.getId(), request);
        return ResponseEntity.ok(new ApiResponse("Order placed!", orderDto));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse> getUserOrders() {
        User user = userService.getAuthenticatedUser();
        List<OrderDto> orders = orderService.getUserOrders(user.getId());

        return ResponseEntity.ok(new ApiResponse("Success!", orders));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse> getAllOrdersForAdmin(
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<OrderDto> orders = orderService.getOrdersForAdmin(status, page, size);
        return ResponseEntity.ok(new ApiResponse("Orders retrieved", orders));
    }

    @PutMapping("/admin/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status
    ) {
        OrderDto updated = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(new ApiResponse("Order status updated", updated));
    }
}
