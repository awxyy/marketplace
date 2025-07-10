package com.dotdot.marketplace.orderitem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemRequestDto {
        @NotNull(message = "Order ID cannot be null")
        private Long orderId;

        @NotNull(message = "Product ID cannot be null")
        private Long productId;

        @Min(value = 1, message = "Quantity must be at least 1")
        private int quantity;
}
