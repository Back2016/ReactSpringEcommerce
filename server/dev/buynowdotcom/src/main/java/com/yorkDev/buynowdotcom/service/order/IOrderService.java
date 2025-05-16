package com.yorkDev.buynowdotcom.service.order;

import com.yorkDev.buynowdotcom.dtos.OrderDto;
import com.yorkDev.buynowdotcom.model.Order;
import com.yorkDev.buynowdotcom.request.PlaceOrderRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IOrderService {
    OrderDto placeOrder(Long userId, PlaceOrderRequest request);
    List<OrderDto> getUserOrders(Long userId);
    OrderDto convertToDto(Order order);
    Page<OrderDto> getOrdersForAdmin(String status, int page, int size);
    OrderDto updateOrderStatus(Long orderId, String status);
}
