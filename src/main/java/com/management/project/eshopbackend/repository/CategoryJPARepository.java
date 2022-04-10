package com.management.project.eshopbackend.repository;

import com.management.project.eshopbackend.models.products.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJPARepository extends JpaRepository<Category, Long> {
  //Included all find,findAll,save... In JpaRepository interface from springframework.
}
