package com.yorkDev.buynowdotcom.dtos;

import com.yorkDev.buynowdotcom.model.Order;
import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private List<OrderDto> orders;
    private CartDto cart;
}