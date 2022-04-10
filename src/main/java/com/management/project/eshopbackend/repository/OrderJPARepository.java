package com.management.project.eshopbackend.repository;

import com.management.project.eshopbackend.models.orders.Order;
import com.management.project.eshopbackend.models.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderJPARepository extends JpaRepository<Order, Long> {

    List<Order> findAllByUser(User user);

    List<Order> findAllByPostman(User postman);
}
