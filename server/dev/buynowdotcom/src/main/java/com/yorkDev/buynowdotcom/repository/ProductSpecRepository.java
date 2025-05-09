package com.yorkDev.buynowdotcom.repository;
import com.yorkDev.buynowdotcom.model.ProductSpec;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductSpecRepository extends JpaRepository<ProductSpec, Long> {
    List<ProductSpec> findByProductId(Long id);
}