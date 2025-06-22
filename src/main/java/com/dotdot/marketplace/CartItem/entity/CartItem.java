package com.dotdot.marketplace.CartItem.entity;

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
    private User userId;

    @ManyToOne()
    @JoinColumn(name = "product_id")
    private User productId;

    @Min(value = 1)
    private int quantity;

    @Column(name = "added_at")
    private LocalDateTime addedAt;


    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be a positive number");
        }
        this.quantity = quantity;
    }
}
