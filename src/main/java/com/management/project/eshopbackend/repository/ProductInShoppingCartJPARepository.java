package com.management.project.eshopbackend.repository;

import com.management.project.eshopbackend.models.shopping_cart.ProductInShoppingCart;
import com.management.project.eshopbackend.models.shopping_cart.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductInShoppingCartJPARepository extends JpaRepository<ProductInShoppingCart, Long> {
    void deleteAllByShoppingCart(ShoppingCart shoppingCart);
    void deleteAllByIdAndShoppingCart(Long id, ShoppingCart shoppingCart);
}
