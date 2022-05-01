package com.management.project.eshopbackend.service.intef;

import com.management.project.eshopbackend.models.shopping_cart.DTO.ProductInShoppingCartDTO;
import com.management.project.eshopbackend.models.shopping_cart.DTO.ShoppingCartDTO;
import com.management.project.eshopbackend.models.shopping_cart.ShoppingCart;

public interface ShoppingCartService {

    ShoppingCart getShoppingCart(String username);
    ShoppingCart addProductToShoppingCart(ProductInShoppingCartDTO productInShoppingCartDTO, String username);
    void deleteProductFromShoppingCart(Long id, String username);
    ShoppingCartDTO convertToDTO(ShoppingCart shoppingCart);
}
