package com.dotdot.marketplace.product.service;

import com.dotdot.marketplace.product.dto.ProductRequestDto;
import com.dotdot.marketplace.product.dto.ProductResponseDto;

import java.util.List;

public interface ProductService {

    ProductResponseDto create(ProductRequestDto request);

    ProductResponseDto getById(long id);

    List<ProductResponseDto> getAll();

    ProductResponseDto update(long id, ProductRequestDto request);

    void delete(long id);
}
