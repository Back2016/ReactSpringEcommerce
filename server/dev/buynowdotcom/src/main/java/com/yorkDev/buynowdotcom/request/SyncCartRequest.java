package com.yorkDev.buynowdotcom.request;

import lombok.Data;

import java.util.List;

@Data
public class SyncCartRequest {
    private List<SyncCartItem> items;

    @Data
    public static class SyncCartItem {
        private Long productId;
        private int quantity;
    }
}
