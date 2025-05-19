package com.yorkDev.buynowdotcom.request;

import com.yorkDev.buynowdotcom.dtos.AddressDto;
import com.yorkDev.buynowdotcom.dtos.CartItemDto;
import lombok.Data;

import java.util.List;

@Data
public class PlaceGuestOrderRequest {
    private String guestEmail;
    private AddressDto shippingAddress;
    private AddressDto billingAddress;
    private List<CartItemDto> items;
}

