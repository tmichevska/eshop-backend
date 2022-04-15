package com.management.project.eshopbackend.service.impl;

import com.management.project.eshopbackend.models.exceptions.EntityNotFoundException;
import com.management.project.eshopbackend.models.shopping_cart.DTO.ProductInShoppingCartDTO;
import com.management.project.eshopbackend.models.shopping_cart.DTO.ShoppingCartDTO;
import com.management.project.eshopbackend.models.shopping_cart.ProductInShoppingCart;
import com.management.project.eshopbackend.models.shopping_cart.ShoppingCart;
import com.management.project.eshopbackend.models.products.Product;
import com.management.project.eshopbackend.models.users.User;
import com.management.project.eshopbackend.repository.ProductInShoppingCartJPARepository;
import com.management.project.eshopbackend.repository.ProductJPARepository;
import com.management.project.eshopbackend.repository.ShoppingCartJPARepository;
import com.management.project.eshopbackend.repository.UserJPARepository;
import com.management.project.eshopbackend.service.intef.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartJPARepository shoppingCartJPARepository;
    private final ProductInShoppingCartJPARepository productInShoppingCartJPARepository;
    private final UserJPARepository userJPARepository;
    private final ProductJPARepository productJPARepository;

    @Override
    public ShoppingCart getShoppingCart(String username) {

        User user = userJPARepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        ShoppingCart shoppingCart = shoppingCartJPARepository.findByUser_Id(user.getId()).orElse(new ShoppingCart(user));
        shoppingCartJPARepository.save(shoppingCart);
        return shoppingCart;
    }

    @Override
    public ShoppingCart addProductToShoppingCart(ProductInShoppingCartDTO productInShoppingCartDTO, String username) {

        User user = userJPARepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        ShoppingCart shoppingCart = shoppingCartJPARepository.findByUser_Id(user.getId()).orElseThrow(() -> new EntityNotFoundException("Shopping cart not found!"));
        ProductInShoppingCart pisc = shoppingCart.getProductsInShoppingCart().stream()
                .filter(p -> p.getProduct().getId().equals(productInShoppingCartDTO.getProductId()))
                .findFirst().orElse(null);
        if(pisc==null)
            return this.build(productInShoppingCartDTO, user).getShoppingCart();
        else{
            pisc.setQuantity(pisc.getQuantity() + 1);
            productInShoppingCartJPARepository.save(pisc);
            return pisc.getShoppingCart();
        }
    }

    @Override
    public void deleteProductFromShoppingCart(Long id, String username) {

        ShoppingCart shoppingCart = this.getShoppingCart(username);
        ProductInShoppingCart productInShoppingCart = productInShoppingCartJPARepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("product in shopping cart with id: " + id.toString() + "not found!"));
        productInShoppingCartJPARepository.deleteAllByIdAndShoppingCart(id, shoppingCart);
//        productInShoppingCartJPARepository.delete(productInShoppingCart);
    }

    @Override
    public ShoppingCartDTO convertToDTO(ShoppingCart shoppingCart) {
        return ShoppingCart.convertToDTO(shoppingCart);
    }

    private ProductInShoppingCart build(ProductInShoppingCartDTO productInShoppingCartDTO, User user) {

        Product product = productJPARepository.findById(productInShoppingCartDTO.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product with id " + productInShoppingCartDTO.getProductId() + " not found!"));
        ProductInShoppingCart productInShoppingCart = ProductInShoppingCart.builder()
                .shoppingCart(this.getShoppingCart(user.getUsername()))
                .product(product)
                .quantity(productInShoppingCartDTO.getQuantity())
                .dateCreated(LocalDateTime.now())
                .build();
        productInShoppingCartJPARepository.save(productInShoppingCart);
        return productInShoppingCart;
    }

}
