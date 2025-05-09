package com.yorkDev.buynowdotcom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ProductSpec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String specName;
    private String value;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}