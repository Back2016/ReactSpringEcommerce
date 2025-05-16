package com.yorkDev.buynowdotcom.repository;

import com.yorkDev.buynowdotcom.model.Address;
import com.yorkDev.buynowdotcom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user);
}
