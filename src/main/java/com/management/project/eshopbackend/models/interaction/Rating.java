package com.management.project.eshopbackend.models.interaction;

import com.management.project.eshopbackend.models.products.Product;
import com.management.project.eshopbackend.models.users.User;
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
@Table(name = "Ratings")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double grade;

    @ManyToOne
    private User user;

    @ManyToOne
    private Product product;

    private LocalDateTime dateCreated;
}