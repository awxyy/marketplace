package com.dotdot.marketplace.product.dto;

import com.dotdot.marketplace.user.entity.User;
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
    private User seller;
    private String status;
    private LocalDateTime createdAt;

}
