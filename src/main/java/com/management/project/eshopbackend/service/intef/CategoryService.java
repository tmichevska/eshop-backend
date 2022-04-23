package com.management.project.eshopbackend.service.intef;

import com.management.project.eshopbackend.models.products.Category;
import com.management.project.eshopbackend.models.products.DTO.CategoryDTO;

import java.util.List;

public interface CategoryService {
    Category findById(Long id);
    List<Category> findAll();
    Category create(CategoryDTO categoryDTO);
    Category update(CategoryDTO categoryDTO);
    void delete(Long id);
}
