package com.yorkDev.buynowdotcom.service.order;

import com.stripe.exception.StripeException;
import com.yorkDev.buynowdotcom.dtos.OrderDto;
import com.yorkDev.buynowdotcom.model.Order;
import com.yorkDev.buynowdotcom.request.PaymentRequest;
import com.yorkDev.buynowdotcom.request.PlaceGuestOrderRequest;
import com.yorkDev.buynowdotcom.request.PlaceOrderRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IOrderService {
    OrderDto placeOrder(Long userId, PlaceOrderRequest request);
    List<OrderDto> getUserOrders(Long userId);
    OrderDto convertToDto(Order order);
    Page<OrderDto> getOrdersForAdmin(String status, int page, int size);
    OrderDto updateOrderStatus(Long orderId, String status);
    OrderDto placeGuestOrder(PlaceGuestOrderRequest request);
    List<Order> getGuestOrdersByEmail(String email);
    Order getOrderById(Long orderId);
    String createPaymentIntent(PaymentRequest request) throws StripeException;
}
