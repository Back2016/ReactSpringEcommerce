package com.yorkDev.buynowdotcom.request;

import lombok.Data;

@Data
public class PlaceOrderRequest {
    private Long shippingAddressId;
    private Long billingAddressId;
}

