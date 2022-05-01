package com.management.project.eshopbackend.web.rest;

import com.management.project.eshopbackend.models.exceptions.*;
import com.management.project.eshopbackend.models.orders.DTO.OrderDto;
import com.management.project.eshopbackend.models.orders.DTO.ResponseOrderDTO;
import com.management.project.eshopbackend.models.orders.DTO.UpdateOrderStatusDTO;
import com.management.project.eshopbackend.models.orders.Order;
import com.management.project.eshopbackend.models.products.*;
import com.management.project.eshopbackend.service.intef.OrderService;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@CrossOrigin(value = "*")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllOrders() {
        List<ResponseOrderDTO> responseOrderDTOS;
        try {
            List<Order> orders = orderService.getAllOrders();
            responseOrderDTOS = orders.stream().map(Order::convertToDto).collect(Collectors.toList());
        } catch (UsernameNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(responseOrderDTOS, HttpStatus.OK);
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> getOrders(@PathVariable String username) {

        List<Order> orders;
        List<ResponseOrderDTO> responseOrderDTOS;
        try {
            orders = this.orderService.getOrders(username);
            responseOrderDTOS = orders.stream().map(Order::convertToDto).collect(Collectors.toList());
        } catch (UsernameNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(responseOrderDTOS, HttpStatus.OK);
    }

    @GetMapping("/postman/{postman}")
    @PreAuthorize("hasRole('POSTMAN')")
    public ResponseEntity<?> getOrdersByPostman(@PathVariable String postman) {

        List<Order> orders;
        List<ResponseOrderDTO> responseOrderDTOS;
        try {
            orders = this.orderService.getOrdersByPostman(postman);
            responseOrderDTOS = orders.stream().map(Order::convertToDto).collect(Collectors.toList());
        } catch (UsernameNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(responseOrderDTOS, HttpStatus.OK);
    }

    @PutMapping("/update-status")
    @PreAuthorize("hasAnyRole('POSTMAN', 'ADMIN')")
    public ResponseEntity<?> updateOrderStatus(@RequestBody UpdateOrderStatusDTO updateOrderStatusDto) {
        ResponseOrderDTO responseOrderDTO;
        try {
            Order order = orderService.changeOrderStatus(updateOrderStatusDto);
            responseOrderDTO = Order.convertToDto(order);
        } catch (UsernameNotFoundException | EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(responseOrderDTO, HttpStatus.OK);
    }
}
