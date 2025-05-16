package com.yorkDev.buynowdotcom.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressDto {
    private Long id; // only used in responses

    @NotBlank
    private String recipientName;

    @NotBlank
    private String street;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String country;

    @NotBlank
    private String zipcode;

    @NotBlank
    @Size(min = 8, max = 20)
    private String phone;

    private boolean defaultShipping;
    private boolean defaultBilling;
}
