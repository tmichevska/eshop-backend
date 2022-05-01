package com.management.project.eshopbackend.service.impl;

import com.management.project.eshopbackend.models.exceptions.EntityNotFoundException;
import com.management.project.eshopbackend.models.products.Category;
import com.management.project.eshopbackend.models.products.DTO.CategoryDTO;
import com.management.project.eshopbackend.repository.CategoryJPARepository;
import com.management.project.eshopbackend.service.intef.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryJPARepository categoryRepository;

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " not found!"));
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category create(CategoryDTO categoryDTO) {
        Category category = Category.builder()
                .name(categoryDTO.getName())
                .attributes(new LinkedList<>())
                .products(new LinkedList<>())
                .dateCreated(LocalDateTime.now())
                .build();
        return categoryRepository.save(category);
    }

    @Override
    public Category update(CategoryDTO categoryDTO) {
        Category category = findById(categoryDTO.getId());
        category.setName(categoryDTO.getName());
        return categoryRepository.save(category);
    }

    @Override
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
