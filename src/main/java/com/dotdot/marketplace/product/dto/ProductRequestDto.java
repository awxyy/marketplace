package com.dotdot.marketplace.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @Min(value = 1, message = "Price must be greater than 0")
    private double price;

    @NotNull(message = "sellerId required")
    private Long sellerId;
    private int quantity;
}
