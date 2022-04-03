package com.management.project.eshopbackend.models.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Postman {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private User user;

    private String city;

    private Integer ordersToDeliver;

    //    ne znam dali count ke treba
    public Postman(User user, String city) {
        this.user = user;
        this.city = city;
        this.ordersToDeliver = 0;
    }

    public void updateCount() {
        this.ordersToDeliver++;
    }

    public void decreaseOrderCount() {
        this.ordersToDeliver--;
    }
}
