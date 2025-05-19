package com.yorkDev.buynowdotcom.repository;

import com.yorkDev.buynowdotcom.enums.OrderStatus;
import com.yorkDev.buynowdotcom.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    Page<Order> findByOrderStatus(OrderStatus status, Pageable pageable);
    List<Order> findByGuestEmail(String guestEmail);
}
