package com.management.project.eshopbackend.models.orders.DTO;

import com.management.project.eshopbackend.models.enumerations.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UpdateOrderStatusDTO {

    private Long orderId;
    private OrderStatus orderStatus;
}
