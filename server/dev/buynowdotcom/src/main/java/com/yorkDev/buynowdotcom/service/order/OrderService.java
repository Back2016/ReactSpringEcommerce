package com.yorkDev.buynowdotcom.service.order;

import com.yorkDev.buynowdotcom.dtos.AddressDto;
import com.yorkDev.buynowdotcom.dtos.CartItemDto;
import com.yorkDev.buynowdotcom.dtos.OrderDto;
import com.yorkDev.buynowdotcom.enums.OrderStatus;
import com.yorkDev.buynowdotcom.exceptions.InsufficientInventoryException;
import com.yorkDev.buynowdotcom.model.*;
import com.yorkDev.buynowdotcom.repository.AddressRepository;
import com.yorkDev.buynowdotcom.repository.OrderRepository;
import com.yorkDev.buynowdotcom.repository.ProductRepository;
import com.yorkDev.buynowdotcom.request.PlaceGuestOrderRequest;
import com.yorkDev.buynowdotcom.request.PlaceOrderRequest;
import com.yorkDev.buynowdotcom.service.cart.ICartService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public OrderDto placeOrder(Long userId, PlaceOrderRequest request) {
        Cart cart = cartService.getCartByUserId(userId);
        Address shipping = addressRepository.findById(request.getShippingAddressId())
                .orElseThrow(() -> new EntityNotFoundException("Shipping address not found"));

        Address billing = addressRepository.findById(request.getBillingAddressId())
                .orElseThrow(() -> new EntityNotFoundException("Billing address not found"));

        // Create an order under current user
        Order order = createOrder(cart, shipping, billing);
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
    private Order createOrder(Cart cart, Address shipping, Address billing) {
        Order order = new Order();
        order.setShippingAddress(shipping);
        order.setBillingAddress(billing);
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
    @Transactional
    public OrderDto placeGuestOrder(PlaceGuestOrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        Address shipping = saveGuestAddress(request.getShippingAddress());
        Address billing = saveGuestAddress(request.getBillingAddress());

        Order order = createGuestOrder(request.getGuestEmail(), shipping, billing);

        List<OrderItem> orderItems = createGuestOrderItems(order, request.getItems());

        order.setOrderItems(new HashSet<>(orderItems));
        order.setTotalAmount(calculateTotalAmount(orderItems));

        Order saved = orderRepository.save(order);
        return convertToDto(saved);
    }

    private Address saveGuestAddress(AddressDto addressDto) {
        Address address = modelMapper.map(addressDto, Address.class);
        return addressRepository.save(address);
    }

    private Order createGuestOrder(String guestEmail, Address shipping, Address billing) {
        Order order = new Order();
        order.setGuestEmail(guestEmail);
        order.setShippingAddress(shipping);
        order.setBillingAddress(billing);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDate.now());
        return order;
    }

    private List<OrderItem> createGuestOrderItems(Order order, List<CartItemDto> items) {
        return items.stream().map(item -> {
            Long productId = item.getProduct().getId();
            int quantity = item.getQuantity();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

            if (product.getInventory() < quantity) {
                throw new InsufficientInventoryException("Insufficient stock for product: " + product.getName());
            }

            product.setInventory(product.getInventory() - quantity);
            productRepository.save(product);

            return new OrderItem(order, product, product.getPrice(), quantity);
        }).toList();
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
    }


    @Override
    public List<Order> getGuestOrdersByEmail(String email) {
        return orderRepository.findByGuestEmail(email);
    }


    @Override
    public List<OrderDto> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(this::convertToDto).toList();
    }

    @Override
    public OrderDto convertToDto(Order order) {
        OrderDto dto = modelMapper.map(order, OrderDto.class);

        // Explicitly map nested addresses
        dto.setShippingAddress(modelMapper.map(order.getShippingAddress(), AddressDto.class));
        dto.setBillingAddress(modelMapper.map(order.getBillingAddress(), AddressDto.class));

        return dto;
    }

    @Override
    public Page<OrderDto> getOrdersForAdmin(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> orders;

        if ("ALL".equalsIgnoreCase(status)) {
            orders = orderRepository.findAll(pageable);
        } else {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            orders = orderRepository.findByOrderStatus(orderStatus, pageable);
        }

        return orders.map(this::convertToDto);
    }

    @Override
    @Transactional
    public OrderDto updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }

        order.setOrderStatus(newStatus);
        Order updated = orderRepository.save(order);

        return convertToDto(updated);
    }


}
