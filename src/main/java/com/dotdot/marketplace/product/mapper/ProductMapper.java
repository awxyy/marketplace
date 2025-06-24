package com.dotdot.marketplace.product.mapper;

import com.dotdot.marketplace.product.dto.ProductRequestDto;
import com.dotdot.marketplace.product.entity.Product;
import com.dotdot.marketplace.product.entity.ProductStatus;

public class ProductMapper {

    public static Product toEntity(ProductRequestDto dto) {
        Product product = new Product();

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStatus(ProductStatus.AVAILABLE);
        return product;
    }

}
