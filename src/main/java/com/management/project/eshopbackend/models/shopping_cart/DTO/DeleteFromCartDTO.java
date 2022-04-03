package com.management.project.eshopbackend.models.shopping_cart.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DeleteFromCartDTO {

    private final Long productId;

    private final String username;
}
