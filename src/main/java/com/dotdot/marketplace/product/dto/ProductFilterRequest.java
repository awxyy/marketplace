package com.dotdot.marketplace.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterRequest {

    private String name;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    @Builder.Default
    private int page = 0;
    
    @Builder.Default
    private int size = 10;

    @Builder.Default
    private String sortBy = "createdAt";
    
    @Builder.Default
    private String direction = "asc";

    public static ProductFilterRequest of(
            String name,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size,
            String sortBy,
            String direction) {
        
        return ProductFilterRequest.builder()
                .name(name)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .page(page)
                .size(size)
                .sortBy(sortBy != null ? sortBy : "createdAt")
                .direction(direction != null ? direction : "asc")
                .build();
    }
}
