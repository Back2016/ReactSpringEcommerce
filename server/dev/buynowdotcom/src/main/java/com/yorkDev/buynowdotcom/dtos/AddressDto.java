package com.yorkDev.buynowdotcom.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Size(min = 5, max = 6, message = "Zipcode must be between 5 and 6 digits")
    @Pattern(regexp = "\\d+", message = "Zipcode must contain only numbers")
    private String zipcode;

    @NotBlank
    @Size(min = 8, max = 20, message = "Phone number must be between 8 and 20 digits")
    @Pattern(regexp = "\\d+", message = "Phone number must contain only numbers")
    private String phone;

    private boolean defaultShipping;
    private boolean defaultBilling;
}
