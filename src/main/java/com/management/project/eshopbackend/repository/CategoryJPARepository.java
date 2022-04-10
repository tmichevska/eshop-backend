package com.management.project.eshopbackend.repository;

import com.management.project.eshopbackend.models.products.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJPARepository extends JpaRepository<Category, Long> {
}
