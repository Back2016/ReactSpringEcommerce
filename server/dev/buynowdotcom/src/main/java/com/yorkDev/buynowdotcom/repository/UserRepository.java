package com.yorkDev.buynowdotcom.repository;

import com.yorkDev.buynowdotcom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}
