package com.management.project.eshopbackend.service.impl;

import com.management.project.eshopbackend.models.exceptions.EntityNotFoundException;
import com.management.project.eshopbackend.models.products.Attribute;
import com.management.project.eshopbackend.models.products.Category;
import com.management.project.eshopbackend.models.products.DTO.AttributeDTO;
import com.management.project.eshopbackend.repository.AttributeJPARepository;
import com.management.project.eshopbackend.repository.CategoryJPARepository;
import com.management.project.eshopbackend.service.intef.AttributeService;
import com.management.project.eshopbackend.service.intef.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AttributeServiceImpl implements AttributeService {

    private final AttributeJPARepository attributeRepository;
    private final CategoryJPARepository categoryRepository;
    private final ProductService productService;

    @Override
    public Attribute findById(Long id) {
        return attributeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attribute with id " + id + " not found!"));
    }

    @Override
    public List<Attribute> findAll() {
        return attributeRepository.findAll();
    }

    @Override
    public List<Attribute> findAttributesByCategoryId(Long id) {
        categoryRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " not found!"));
        return attributeRepository.findAttributesByCategoryId(id);
    }

    @Override
    public Attribute create(AttributeDTO attributeDTO) {
        Category category = categoryRepository
                .findById(attributeDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + attributeDTO.getCategoryId() + " not found!"));
        Attribute newAttribute = Attribute.builder()
                .category(category)
                .name(attributeDTO.getName())
                .suffix(attributeDTO.getSuffix())
                .isNumeric(attributeDTO.isNumeric())
                .dateCreated(LocalDateTime.now())
                .build();
        Attribute savedAttribute = attributeRepository.save(newAttribute);
        productService.updateProductAttributesForCategoryId(category.getId());
        return savedAttribute;
    }

    @Override
    public Attribute update(AttributeDTO attributeDTO) {
        Category category = categoryRepository
                .findById(attributeDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + attributeDTO.getCategoryId() + " not found!"));
        Attribute attribute = findById(attributeDTO.getId());
        attribute.setName(attributeDTO.getName());
        attribute.setSuffix(attributeDTO.getSuffix());
        attribute.setNumeric(attributeDTO.isNumeric());
        attribute.setCategory(category);
        Attribute updatedAttribute = attributeRepository.save(attribute);
        productService.updateProductAttributesForCategoryId(category.getId());
        return updatedAttribute;
    }

    @Override
    public void delete(Long id) {
        Attribute deletedAttribute = findById(id);
        attributeRepository.deleteById(id);
        productService.updateProductAttributesForCategoryId(deletedAttribute.getCategory().getId());
    }
}
