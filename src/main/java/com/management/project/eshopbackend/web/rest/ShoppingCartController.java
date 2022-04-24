package com.management.project.eshopbackend.web.rest;

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
}
