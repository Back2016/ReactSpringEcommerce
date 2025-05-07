package com.yorkDev.buynowdotcom.service.cart;

import com.yorkDev.buynowdotcom.dtos.CartDto;
import com.yorkDev.buynowdotcom.model.Cart;
import com.yorkDev.buynowdotcom.model.User;

import java.math.BigDecimal;

public interface ICartService {
    Cart getCart(Long cartId);
    Cart getCartByUserId(Long userId);
    CartDto getCartDtoByUserId(Long userId);
    void clearCart(Long cartId);
    Cart initializeNewCartForUser(User user);
    BigDecimal getTotalPrice(Long cartId);
    CartDto convertToDto(Cart cart);
}
