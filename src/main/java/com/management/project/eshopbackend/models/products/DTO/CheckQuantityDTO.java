package com.management.project.eshopbackend.models.products.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CheckQuantityDTO {
    Long productId;
    int quantity;
}
