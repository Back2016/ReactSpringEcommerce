package com.yorkDev.buynowdotcom.controller;

import com.yorkDev.buynowdotcom.model.Cart;
import com.yorkDev.buynowdotcom.model.User;
import com.yorkDev.buynowdotcom.response.ApiResponse;
import com.yorkDev.buynowdotcom.service.cart.ICartItemService;
import com.yorkDev.buynowdotcom.service.cart.ICartService;
import com.yorkDev.buynowdotcom.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/cartItems")
public class CartItemController {
    private final ICartItemService cartItemService;
    private final IUserService userService;
    private final ICartService cartService;

    @PostMapping("/cartItem/add")
    public ResponseEntity<ApiResponse> addItemToCart(@RequestParam Long productId,
                                                     @RequestParam int quantity) {
        User user = userService.getAuthenticatedUser();
        Cart cart = cartService.initializeNewCartForUser(user);
        cartItemService.addItemToCart(cart.getId(), productId, quantity);
        return ResponseEntity.ok(new ApiResponse("Item added successfully!", null));
    }

    @DeleteMapping("/cart/delete/item/{productId}")
    public ResponseEntity<ApiResponse> removeItemFromCart(
                                                          @PathVariable Long productId) {
        User user = userService.getAuthenticatedUser();
        Cart cart = cartService.initializeNewCartForUser(user);
        cartItemService.removeItemFromCart(cart.getId(), productId);
        return ResponseEntity.ok(new ApiResponse("Item removed successfully!", null));
    }

    @PutMapping("/cart/update/item/{productId}")
    public ResponseEntity<ApiResponse> updateCartItem(
                                                      @PathVariable Long productId,
                                                      @RequestParam int quantity) {
        User user = userService.getAuthenticatedUser();
        Cart cart = cartService.initializeNewCartForUser(user);
        cartItemService.updateItemQuantity(cart.getId(), productId, quantity);
        return ResponseEntity.ok(new ApiResponse("Cart item updated successfully!", null));
    }
}
