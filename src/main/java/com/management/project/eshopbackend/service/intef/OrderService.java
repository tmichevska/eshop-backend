package com.management.project.eshopbackend.service.intef;

import com.management.project.eshopbackend.models.orders.DTO.OrderDto;
import com.management.project.eshopbackend.models.orders.DTO.ResponseOrderDTO;
import com.management.project.eshopbackend.models.orders.DTO.UpdateOrderStatusDTO;
import com.management.project.eshopbackend.models.orders.Order;

import java.util.List;

public interface OrderService {
    List<Order> getOrders(String username);

    List<Order> getAllOrders();

    List<Order> getOrdersByPostman(String postmanUsername);

    Order makeOrder(OrderDto orderDto);

    ResponseOrderDTO convertToDto(Order order);

    Order changeOrderStatus(UpdateOrderStatusDTO updateOrderStatusDTO);
}
