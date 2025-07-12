package com.dotdot.marketplace.cartitem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponseDto {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private LocalDateTime addedAt;
}
