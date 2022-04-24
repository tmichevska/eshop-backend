package com.management.project.eshopbackend.web.rest;

import com.management.project.eshopbackend.models.exceptions.EntityNotFoundException;
import com.management.project.eshopbackend.models.shopping_cart.DTO.DeleteFromCartDTO;
import com.management.project.eshopbackend.models.shopping_cart.DTO.ProductInShoppingCartDTO;
import com.management.project.eshopbackend.models.shopping_cart.DTO.ShoppingCartDTO;
import com.management.project.eshopbackend.models.shopping_cart.ShoppingCart;
import com.management.project.eshopbackend.service.intef.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shoppingCart")
@CrossOrigin(value = "*")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @GetMapping("/{username}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> getShoppingCart(@PathVariable String username) {

        ShoppingCart shoppingCart;
        ShoppingCartDTO shoppingCartDTO;
        try {
            shoppingCart = this.shoppingCartService.getShoppingCart(username);
            shoppingCartDTO = this.shoppingCartService.convertToDTO(shoppingCart);
        } catch (UsernameNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(shoppingCartDTO, HttpStatus.OK);
    }
    @PostMapping("/deleteProduct")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> deleteProductFromShoppingCart(@RequestBody DeleteFromCartDTO deleteFromCartDTO) {
        String username = deleteFromCartDTO.getUsername();
        Long productId = deleteFromCartDTO.getProductId();
        try {
            shoppingCartService.deleteProductFromShoppingCart(productId, username);
        }
        catch (UsernameNotFoundException | EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Product in shopping cart with id " + productId + " deleted.", HttpStatus.OK);
    }
    @PostMapping("/{username}/addToShoppingCart")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> addProductToShoppingCart(@PathVariable String username,
                                                      @RequestBody ProductInShoppingCartDTO productInShoppingCartDTO) {
        ShoppingCart shoppingCart;
        try{
            shoppingCart = shoppingCartService.addProductToShoppingCart(productInShoppingCartDTO, username);
        }
        catch (UsernameNotFoundException | EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
        ShoppingCartDTO shoppingCartDTO = shoppingCartService.convertToDTO(shoppingCart);
        return new ResponseEntity<>(shoppingCartDTO, HttpStatus.OK);
    }
}