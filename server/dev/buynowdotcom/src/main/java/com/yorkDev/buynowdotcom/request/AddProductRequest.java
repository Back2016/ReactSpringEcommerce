package com.yorkDev.buynowdotcom.request;

import com.yorkDev.buynowdotcom.model.Category;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddProductRequest {
    private Long id;
    private String name;
    private String brand;
    private BigDecimal price;
    private int inventory;
    private String description;
    private String longDescription;
    private Category category;
}
