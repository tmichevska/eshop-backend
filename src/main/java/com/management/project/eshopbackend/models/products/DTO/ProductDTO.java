package com.management.project.eshopbackend.models.products.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
public class ProductDTO {
    private Long id;
    @NotNull
    @NotEmpty
    private final Long categoryId;

    private final String categoryName;

    @NotNull
    @NotEmpty
    private final String productTitle;

    private final String productDescriptionHTML;

    private final int quantity;

    @NotNull
    @NotEmpty
    private final Double priceInMKD;

    private final Map<Long, String> attributeIdAndValueMap;

    private final Map<String, List<String>> attributeNameSuffixAndValueMap;

    private final LocalDateTime dateCreated;

    public void setId(Long id){
        this.id=id;
    }
}
