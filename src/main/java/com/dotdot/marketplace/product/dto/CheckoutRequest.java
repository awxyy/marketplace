package com.dotdot.marketplace.product.dto;

import lombok.Data;

@Data
public class CheckoutRequest {
    private Long productId;
    private int quantity;
}
