package com.management.project.eshopbackend.repository;

import com.management.project.eshopbackend.models.products.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttributeJPARepository extends JpaRepository<Attribute, Long> {
    List<Attribute> findAttributesByCategoryId(Long id);
}
