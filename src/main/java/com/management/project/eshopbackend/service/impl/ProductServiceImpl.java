package com.management.project.eshopbackend.service.impl;

import com.management.project.eshopbackend.models.exceptions.EntityNotFoundException;
import com.management.project.eshopbackend.models.products.Product;
import com.management.project.eshopbackend.repository.CategoryJPARepository;
import com.management.project.eshopbackend.repository.ProductJPARepository;
import com.management.project.eshopbackend.repository.AttributeJPARepository;
import com.management.project.eshopbackend.service.intef.ShoppingCartService;
import com.management.project.eshopbackend.service.intef.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductJPARepository productRepository;
    private final CategoryJPARepository categoryRepository;
    private final AttributeJPARepository attributeRepository;
    private final ShoppingCartService shoppingCartService;


    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with id " + id + " not found!"));
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> findAllByCategoryId(Long id) {
        return productRepository.findAll().stream().filter(product -> product.getCategory().getId().equals(id)).collect(Collectors.toList());
    }
}
