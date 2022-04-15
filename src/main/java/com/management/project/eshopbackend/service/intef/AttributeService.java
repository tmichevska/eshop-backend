package com.management.project.eshopbackend.service.intef;

import com.management.project.eshopbackend.models.products.Attribute;
import com.management.project.eshopbackend.models.products.DTO.AttributeDTO;

import java.util.List;

public interface AttributeService {
    Attribute findById(Long id);
    List<Attribute> findAll();
    List<Attribute> findAttributesByCategoryId(Long id);
    Attribute create(AttributeDTO attributeDTO);
    Attribute update(AttributeDTO attributeDTO);
    void delete(Long id);
}
