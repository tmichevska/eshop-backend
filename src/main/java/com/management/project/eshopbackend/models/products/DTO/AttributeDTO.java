package com.management.project.eshopbackend.models.products.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class AttributeDTO {
    private Long id;
    @NotNull
    @NotEmpty
    private final String name;
    private final String suffix;
    @NotNull
    @NotEmpty
    private final Long categoryId;
    @NotNull
    private final boolean numeric;
    private final LocalDateTime dateCreated;

    public void setId(Long id){
        this.id=id;
    }
}
