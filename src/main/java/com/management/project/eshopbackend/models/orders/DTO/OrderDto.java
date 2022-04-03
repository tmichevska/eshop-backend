package com.management.project.eshopbackend.models.orders.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OrderDto {

    private String username;

    private String city;

    private String telephone;

    private String address;
}
