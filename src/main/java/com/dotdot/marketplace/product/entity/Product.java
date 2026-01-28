package com.dotdot.marketplace.product.entity;

import com.dotdot.marketplace.productImage.entity.ProductImage;
import com.dotdot.marketplace.review.entity.Review;
import com.dotdot.marketplace.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "reserved_quantity")
    private int reservedQuantity = 0;

    @ManyToOne
    @JoinColumn(name ="seller")
    private User seller;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "review_count")
    private Long reviewsCount = 0L;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<ProductImage> images = new ArrayList<>();

    public int getAvailableQuantity() {
        return quantity - reservedQuantity;
    }

}
