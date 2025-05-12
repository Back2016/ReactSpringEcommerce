package com.yorkDev.buynowdotcom.repository;

import com.yorkDev.buynowdotcom.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryNameAndBrand(String category, String brand);

    List<Product> findByCategoryName(String category);

    Page<Product> findByCategoryName(String categoryName, Pageable pageable);

    List<Product> findByNameAndBrand(String name, String brand);

    List<Product> findByBrand(String brand);

    // Case-insensitive partial match
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> findByName(String name);

    boolean existsByNameAndBrand(String name, String brand);
}
