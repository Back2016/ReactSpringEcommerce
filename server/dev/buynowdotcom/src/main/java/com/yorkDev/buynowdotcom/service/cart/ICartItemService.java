package com.yorkDev.buynowdotcom.service.cart;

import com.yorkDev.buynowdotcom.model.CartItem;
import com.yorkDev.buynowdotcom.request.SyncCartRequest;

import java.util.List;

public interface ICartItemService {
    void addItemToCart(Long cartId, Long productId, int quantity);
    void removeItemFromCart(Long cartId, Long productId);
    void updateItemQuantity(Long cartId, Long productId, int quantity);
    CartItem getCartItem(Long cartId, Long productId);
    void syncCartItems(Long cartId, List<SyncCartRequest.SyncCartItem> incomingItems);
}
