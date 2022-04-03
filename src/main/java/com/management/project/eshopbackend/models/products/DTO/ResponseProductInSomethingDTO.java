package com.management.project.eshopbackend.models.products.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@Getter
public class ResponseProductInSomethingDTO {
    private Long id;

    private Long productId;
    @NotNull
    @NotEmpty
    private final Long categoryId;

    @NotNull
    @NotEmpty
    private final String productTitle;

    private final String productDescriptionHTML;

    private final int quantity;

    @NotNull
    @NotEmpty
    private final Double priceInMKD;

    private final Map<Long, String> attributeIdAndValueMap;

    private final LocalDateTime dateCreated;
}
