package com.dotdot.marketplace.product.repository;

import com.dotdot.marketplace.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository  extends JpaRepository<Product, Long> {
}
