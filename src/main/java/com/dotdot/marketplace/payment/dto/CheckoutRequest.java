package com.dotdot.marketplace.payment.dto;

import lombok.Data;

@Data
public class CheckoutRequest {
    private Long productId;
    private int quantity;
}
