package com.dotdot.marketplace.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {

    private long id;
    private String name;
    private String description;
    private double price;
    private Long sellerId;
    private String status;
    private LocalDateTime createdAt;
    private double averageRating;
    private long reviewCount;
    private int quantity;
}
