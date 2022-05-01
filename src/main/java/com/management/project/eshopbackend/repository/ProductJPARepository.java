package com.management.project.eshopbackend.repository;

import com.management.project.eshopbackend.models.products.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJPARepository extends JpaRepository<Product, Long> {
}
