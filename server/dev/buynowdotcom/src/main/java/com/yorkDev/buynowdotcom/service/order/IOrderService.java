package com.yorkDev.buynowdotcom.service.order;

import com.yorkDev.buynowdotcom.dtos.OrderDto;
import com.yorkDev.buynowdotcom.model.Order;

import java.util.List;

public interface IOrderService {
    OrderDto placeOrder(Long userId);
    List<OrderDto> getUserOrders(Long userId);

    OrderDto convertToDto(Order order);
}
