package com.yorkDev.buynowdotcom.service.order;

import com.yorkDev.buynowdotcom.dtos.OrderDto;
import com.yorkDev.buynowdotcom.enums.OrderStatus;
import com.yorkDev.buynowdotcom.exceptions.InsufficientInventoryException;
import com.yorkDev.buynowdotcom.model.*;
import com.yorkDev.buynowdotcom.repository.OrderRepository;
import com.yorkDev.buynowdotcom.repository.ProductRepository;
import com.yorkDev.buynowdotcom.service.cart.ICartService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ICartService cartService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDto placeOrder(Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        // Create an order under current user
        Order order = createOrder(cart);
        // Create a list of orderItems from cart
        List<OrderItem> orderItemList = createOrderItems(order, cart);
        // Set the order items using the list
        order.setOrderItems(new HashSet<>(orderItemList));
        // Calculate total amount
        order.setTotalAmount(calculateTotalAmount(orderItemList));
        // Save to repo
        Order savedOrder = orderRepository.save(order);
        // Clear the cart
        cartService.clearCart(cart.getId());

        return convertToDto(savedOrder);
    }

    // Helper: Create an order under given user with a pending status and now date
    private Order createOrder(Cart cart) {
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDate.now());
        return order;
    }

    private void validateCartInventory(Cart cart) {
        for (CartItem item : cart.getCartItems()) {
            Product product = item.getProduct();
            if (product.getInventory() < item.getQuantity()) {
                throw new InsufficientInventoryException(
                        "Not enough inventory for product: " + product.getName()
                );
            }
        }
    }

    private List<OrderItem> createOrderItems(Order order, Cart cart) {
        validateCartInventory(cart); // Make sure enough inventory for each cartItem
        if (cart.getCartItems().isEmpty()) throw new EntityNotFoundException("Your cart is empty!");
        return cart.getCartItems().stream().map(cartItem -> {
            Product product = cartItem.getProduct();
            // Remove the amount from product inventory
            product.setInventory(product.getInventory() - cartItem.getQuantity());
            productRepository.save(product);
            return new OrderItem(
                    order,
                    product,
                    cartItem.getUnitPrice(),
                    cartItem.getQuantity()
            );
        }).toList();
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> orderItemList) {
        return orderItemList.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<OrderDto> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(this :: convertToDto).toList();
    }

    @Override
    public OrderDto convertToDto(Order order) {
        return modelMapper.map(order, OrderDto.class);
    }
}
