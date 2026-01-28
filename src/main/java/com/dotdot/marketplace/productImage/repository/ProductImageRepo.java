package com.dotdot.marketplace.productImage.repository;


import com.dotdot.marketplace.productImage.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepo extends JpaRepository<ProductImage, Long> {
}
