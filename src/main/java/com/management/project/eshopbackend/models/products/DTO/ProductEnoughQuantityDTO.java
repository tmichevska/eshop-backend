package com.management.project.eshopbackend.models.products.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProductEnoughQuantityDTO {
    boolean hasEnoughQuantity;
    ProductDTO product;
}
