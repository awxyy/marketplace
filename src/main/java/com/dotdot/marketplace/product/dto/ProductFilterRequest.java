package com.dotdot.marketplace.product.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductFilterRequest {

    private String name;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    private int page = 0;
    private int size = 10;

    private String sortBy = "createdAt";
    private String direction = "asc";
}
