package com.yorkDev.buynowdotcom.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipientName;
    private String street;
    private String city;
    private String state;
    private String country;
    private String phone;
    private String zipcode;

    private boolean DefaultShipping;
    private boolean DefaultBilling;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id")
    private User user;
}
