package com.management.project.eshopbackend.repository;

import com.management.project.eshopbackend.models.orders.ProductInOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInOrderJPARepository extends JpaRepository<ProductInOrder, Long> {
}
