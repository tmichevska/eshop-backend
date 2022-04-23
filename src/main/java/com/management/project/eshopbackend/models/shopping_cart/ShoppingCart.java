package com.management.project.eshopbackend.models.shopping_cart;


import com.management.project.eshopbackend.models.products.DTO.ProductDTO;
import com.management.project.eshopbackend.models.products.DTO.ResponseProductInSomethingDTO;
import com.management.project.eshopbackend.models.products.Product;
import com.management.project.eshopbackend.models.shopping_cart.DTO.ShoppingCartDTO;
import com.management.project.eshopbackend.models.users.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "ShoppingCarts")
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    @OneToMany(mappedBy = "shoppingCart", fetch = FetchType.EAGER)
    private List<ProductInShoppingCart> productsInShoppingCart;

    private LocalDateTime dateCreated;

    public ShoppingCart(User user) {
        this.user = user;
        this.productsInShoppingCart = new ArrayList<>();
        this.dateCreated = LocalDateTime.now();
//        this.totalPrice = 0.0;
    }

    public static ShoppingCartDTO convertToDTO(ShoppingCart cart) {
        List<ResponseProductInSomethingDTO> responseProductsInCart = new ArrayList<>();
        for (ProductInShoppingCart product : cart.getProductsInShoppingCart()) {
            ProductDTO productDTO = Product.convertToDTO(product.getProduct());
            ResponseProductInSomethingDTO responseProductInSomethingDTO = new ResponseProductInSomethingDTO(
                    product.getId(),
                    productDTO.getId(),
                    productDTO.getCategoryId(),
                    productDTO.getProductTitle(),
                    productDTO.getProductDescriptionHTML(),
                    product.getQuantity(),
                    productDTO.getPriceInMKD(),
                    productDTO.getAttributeIdAndValueMap(),
                    product.getDateCreated()
            );
            responseProductsInCart.add(responseProductInSomethingDTO);
        }

        return new ShoppingCartDTO(
                cart.getId(),
                cart.getUser().getUsername(),
                responseProductsInCart,
                cart.getDateCreated()
        );
    }
}
