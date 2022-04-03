package com.management.project.eshopbackend.models.shopping_cart;

import com.management.project.eshopbackend.models.products.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "ProductsInShoppingCarts")
public class ProductInShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Product product;

    @ManyToOne
    private ShoppingCart shoppingCart;

    private int quantity;

    private LocalDateTime dateCreated;

    public ProductInShoppingCart(Product product, ShoppingCart shoppingCart, int quantity) {
        this.product = product;
        this.shoppingCart = shoppingCart;
        this.quantity = quantity;
        this.dateCreated = LocalDateTime.now();
    }

    public Double calculatePrice() {
        return quantity * product.getPriceInMKD();
    }
}
