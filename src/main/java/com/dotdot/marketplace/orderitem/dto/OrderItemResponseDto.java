package com.dotdot.marketplace.orderitem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponseDto {

    private Long id;
    private Long orderId;
    private Long productId;
    private int quantity;
    private double priceAtPurchase;
}
