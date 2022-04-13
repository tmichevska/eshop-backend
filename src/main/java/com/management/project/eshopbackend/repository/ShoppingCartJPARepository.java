package com.management.project.eshopbackend.repository;

import com.management.project.eshopbackend.models.shopping_cart.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingCartJPARepository extends JpaRepository<ShoppingCart, Long>{

    Optional<ShoppingCart> findByUser_Id(Long id);
}
