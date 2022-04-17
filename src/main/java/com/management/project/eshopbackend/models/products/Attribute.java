package com.management.project.eshopbackend.models.products;



import com.management.project.eshopbackend.models.products.DTO.AttributeDTO;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Embeddable
@Data
@Table(name = "Attributes")
public class Attribute implements Comparable<Attribute>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotEmpty
    private String name;

    private String suffix;

    @ManyToOne
    @NotNull
    private Category category;

    private LocalDateTime dateCreated;

    private boolean isNumeric;

    public static AttributeDTO convertToDTO(Attribute attribute){
        return new AttributeDTO(attribute.getId(), attribute.getName(), attribute.getSuffix(), attribute.getCategory().getId(), attribute.isNumeric(), attribute.getDateCreated());
    }

    @Override
    public boolean equals(Object obj){
        if (!(obj instanceof Attribute)) return false;

        Attribute temp = (Attribute) obj;
        return this.id.equals(temp.id);
    }

    @Override
    public int hashCode(){
        return id.hashCode();
    }

    @Override
    public int compareTo(Attribute attr){
        return this.dateCreated.compareTo(attr.dateCreated);
    }
}
