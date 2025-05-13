package com.yorkDev.buynowdotcom.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank(message = "Invalid firstName")
    private String firstName;
    @NotBlank(message = "Invalid lastName")
    private String lastName;
    @Email(message = "Invalid email")
    private String email;
    private String password;
}
