package com.yorkDev.buynowdotcom.controller;

import com.yorkDev.buynowdotcom.dtos.CartDto;
import com.yorkDev.buynowdotcom.model.Cart;
import com.yorkDev.buynowdotcom.response.ApiResponse;
import com.yorkDev.buynowdotcom.service.cart.ICartItemService;
import com.yorkDev.buynowdotcom.service.cart.ICartService;
import com.yorkDev.buynowdotcom.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/carts")
public class CartController {
    private final ICartItemService cartItemService;
    private final IUserService userService;
    private final ICartService cartService;

    @GetMapping("/user/{userId}/cart")
    public ResponseEntity<ApiResponse> getUserCart(@PathVariable Long userId) {
        CartDto cartDto = cartService.getCartDtoByUserId(userId);
        return ResponseEntity.ok(new ApiResponse("Found Cart: ", cartDto));
    }

    @DeleteMapping("/user/{userId}/clearCart/{cartId}")
    public ResponseEntity<ApiResponse> clearCart(@PathVariable Long cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.ok(new ApiResponse("Cart cleared! ", null));
    }
}
