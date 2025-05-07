package com.yorkDev.buynowdotcom.service.cart;

import com.yorkDev.buynowdotcom.dtos.CartDto;
import com.yorkDev.buynowdotcom.dtos.CartItemDto;
import com.yorkDev.buynowdotcom.dtos.OrderDto;
import com.yorkDev.buynowdotcom.model.Cart;
import com.yorkDev.buynowdotcom.model.Order;
import com.yorkDev.buynowdotcom.model.User;
import com.yorkDev.buynowdotcom.repository.CartItemRepository;
import com.yorkDev.buynowdotcom.repository.CartRepository;
import com.yorkDev.buynowdotcom.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService{
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public Cart getCart(Long cartId) {
        return cartRepository.findById(cartId).orElseThrow(() -> new EntityNotFoundException("Cart not found!"));
    }

    @Override
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId).orElseThrow(() -> new EntityNotFoundException("Cart not found!"));
    }

    @Override
    public CartDto getCartDtoByUserId(Long userId) {
        Cart cart = getCartByUserId(userId);
        return convertToDto(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long cartId) {
        Cart cart = getCart(cartId);
        cartItemRepository.deleteAllByCartId(cartId);
        cart.clearCart();
        cartRepository.deleteById(cartId);
    }

    @Override
    public Cart initializeNewCartForUser(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    user.setCart(cart);
                    userRepository.save(user); // cascade, so just return user's cart
                    // Do not try to save cart again, because this will insert another user and violate one-to-one
//                    Cart persistedCart = cartRepository.findByUserId(user.getId())
//                            .orElseThrow(() -> new RuntimeException("Cart not saved"));
//                    return cartRepository.save(persistedCart);
                     return user.getCart();
                });
    }

    @Override
    public BigDecimal getTotalPrice(Long cartId) {
        Cart cart = getCart(cartId);

        return cart.getTotalAmount();
    }

    @Override
    public CartDto convertToDto(Cart cart) {
        return modelMapper.map(cart, CartDto.class);
    }
}
