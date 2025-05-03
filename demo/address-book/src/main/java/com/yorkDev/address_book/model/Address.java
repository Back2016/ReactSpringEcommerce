package com.yorkDev.address_book.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Embeddable
public class Address {
    private String country;
    private String state;
    private String city;
    private String address;
    private String postalCode;
}
