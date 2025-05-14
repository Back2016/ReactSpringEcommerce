package com.yorkDev.buynowdotcom.service.cart;

import com.yorkDev.buynowdotcom.exceptions.InsufficientInventoryException;
import com.yorkDev.buynowdotcom.model.Cart;
import com.yorkDev.buynowdotcom.model.CartItem;
import com.yorkDev.buynowdotcom.model.Product;
import com.yorkDev.buynowdotcom.repository.CartItemRepository;
import com.yorkDev.buynowdotcom.repository.CartRepository;
import com.yorkDev.buynowdotcom.request.SyncCartRequest;
import com.yorkDev.buynowdotcom.service.product.IProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemService implements ICartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ICartService cartService;
    private final IProductService productService;

    @Override
    public void addItemToCart(Long cartId, Long productId, int quantity) {
        Cart cart = cartService.getCart(cartId);
        Product product = productService.getProductById(productId);
        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                // If in the cart already, obtain the first (should only be one)
                .findFirst()
                // If not in cart yet, create a new cartItem
                .orElse(new CartItem());

        // If it is a new item, set properties
        if (cartItem.getId() == null) {
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setUnitPrice(product.getPrice());
        }
        // if already there, add the quantity
        else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }

        if (cartItem.getProduct().getInventory() < cartItem.getQuantity())
            throw new InsufficientInventoryException("Not enough products in inventory.");

        cartItem.setTotalPrice();
        cart.addItem(cartItem);
        cartItemRepository.save(cartItem);
        cartRepository.save(cart);
    }

    @Override
    public void removeItemFromCart(Long cartId, Long productId) {
        Cart cart = cartService.getCart(cartId);
        CartItem itemToRemove = getCartItem(cartId, productId);
        cart.removeItem(itemToRemove);
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void updateItemQuantity(Long cartId, Long productId, int quantity) {
        Cart cart = cartService.getCart(cartId);
        cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().ifPresent(item -> {
                    if (item.getProduct().getInventory() < quantity)
                        throw new InsufficientInventoryException("Not enough products in inventory.");
                    item.setQuantity(quantity);
                    item.setUnitPrice(item.getProduct().getPrice());
                    item.setTotalPrice();
                });
        BigDecimal totalAmount = cart.getCartItems().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalAmount(totalAmount);
    }

    @Override
    public CartItem getCartItem(Long cartId, Long productId) {
        Cart cart = cartService.getCart(cartId);
        return cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Item not found in Cart!"));
    }

    @Override
    @Transactional
    public void syncCartItems(Long cartId, List<SyncCartRequest.SyncCartItem> incomingItems) {
        for (SyncCartRequest.SyncCartItem incoming : incomingItems) {
            Long productId = incoming.getProductId();
            int incomingQty = incoming.getQuantity();

            try {
                // If item already exists, get it and update quantity
                CartItem existing = getCartItem(cartId, productId);
                int newQuantity = existing.getQuantity() + incomingQty;
                updateItemQuantity(cartId, productId, newQuantity);
            } catch (EntityNotFoundException e) {
                // If item doesn't exist yet, add it
                addItemToCart(cartId, productId, incomingQty);
            }
        }
    }

}
