package com.management.project.eshopbackend.models.products;


import com.management.project.eshopbackend.models.products.DTO.CategoryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "Categories")
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    @NotNull
    @NotEmpty
    private String name;
    @OneToMany(mappedBy = "category", cascade = {CascadeType.ALL})
    private List<Attribute> attributes;
    @OneToMany(mappedBy = "category")
    private List<Product> products;

    private LocalDateTime dateCreated;

    public static CategoryDTO convertToDTO(Category category){
        return new CategoryDTO(category.getId(), category.getName(), category.getDateCreated());
    }
}
