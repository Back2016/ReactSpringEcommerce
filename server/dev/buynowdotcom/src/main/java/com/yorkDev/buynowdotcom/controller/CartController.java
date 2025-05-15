package com.yorkDev.buynowdotcom.controller;

import com.yorkDev.buynowdotcom.dtos.CartDto;
import com.yorkDev.buynowdotcom.model.Cart;
import com.yorkDev.buynowdotcom.model.User;
import com.yorkDev.buynowdotcom.request.SyncCartRequest;
import com.yorkDev.buynowdotcom.response.ApiResponse;
import com.yorkDev.buynowdotcom.service.cart.ICartItemService;
import com.yorkDev.buynowdotcom.service.cart.ICartService;
import com.yorkDev.buynowdotcom.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/carts")
public class CartController {
    private final ICartItemService cartItemService;
    private final IUserService userService;
    private final ICartService cartService;

    @GetMapping("/user/{userId}/cart")
    public ResponseEntity<ApiResponse> getUserCart(@PathVariable Long userId) {
        // If user id in request and current user does not match, redirect to get current user cart
        User currentUser = userService.getAuthenticatedUser();
        Cart cart = cartService.initializeNewCartForUser(currentUser);
        CartDto cartDto = cartService.getCartDtoByUserId(currentUser.getId());
        return ResponseEntity.ok(new ApiResponse("Found Cart: ", cartDto));
    }

    @DeleteMapping("/user/clearCart")
    public ResponseEntity<ApiResponse> clearCart() {
        User currentUser = userService.getAuthenticatedUser();
        cartService.clearCart(currentUser.getCart().getId());
        return ResponseEntity.ok(new ApiResponse("Cart cleared! ", null));
    }

    @PostMapping("/user/{userId}/syncCart")
    @Transactional
    public ResponseEntity<ApiResponse> syncCart(@PathVariable Long userId, @RequestBody SyncCartRequest request) {
        // If user id in request and current user does not match, redirect to get current user cart
        User currentUser = userService.getAuthenticatedUser();
        // Fetch or create the user's cart
        Cart cart = cartService.initializeNewCartForUser(currentUser);

        cartItemService.syncCartItems(cart.getId(), request.getItems());
        CartDto cartDto = cartService.getCartDtoByUserId(currentUser.getId());

        return ResponseEntity.ok(new ApiResponse("Cart synced successfully", cartDto));
    }
}
