package com.dotdot.marketplace.product.entity;


import com.dotdot.marketplace.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String description;

    private double price;

    @ManyToOne
    @JoinColumn(name ="seller")
    private User seller;

    @ElementCollection
    private ProductStatus status;

}
