package com.management.project.eshopbackend.models.products;


import com.management.project.eshopbackend.models.products.DTO.ProductDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "Products")
@Embeddable
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    private Category category;

    @NotNull
    @NotEmpty
    private String productTitle;

    @Column(length = 4096)
    private String productDescriptionHTML;

    private int quantity;

    @NotNull
    @NotEmpty
    private Double priceInMKD;

    private String pathToMainProductIMG;

    @ElementCollection
    private List<String> pathsToProductIMGs;

    @ElementCollection
    private Map<Attribute, String> valueForProductAttribute;

    private LocalDateTime dateCreated;

    public static ProductDTO convertToDTO(Product product){
        TreeMap<Long, String> attributeValueMap = new TreeMap<>();
        TreeMap<String, List<String>> attributeNameSuffixAndValueMap = new TreeMap<>();
        product.getValueForProductAttribute().forEach((key, value) -> attributeValueMap.put(key.getId(), value));
        product.getValueForProductAttribute()
                .forEach((key, value) ->
                        attributeNameSuffixAndValueMap.put(key.getName(), List.of(key.getSuffix(), value)));
        return new ProductDTO(product.getId(), product.getCategory().getId(), product.getCategory().getName(),
                product.getProductTitle(), product.getProductDescriptionHTML(), product.getQuantity(),
                product.getPriceInMKD(),
                attributeValueMap, attributeNameSuffixAndValueMap, product.getDateCreated());
    }
}
