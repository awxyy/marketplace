package com.dotdot.marketplace.CartItem.entity;

import com.dotdot.marketplace.product.entity.Product;
import com.dotdot.marketplace.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne()
    @JoinColumn(name = "product_id")
    private Product product;

    @Min(value = 1 ,message = "Quantity must be at least 1")
    private int quantity;

    @Column(name = "added_at")
    private LocalDateTime addedAt;


}
