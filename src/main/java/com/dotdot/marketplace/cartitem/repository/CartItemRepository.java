package com.dotdot.marketplace.cartitem.repository;


import com.dotdot.marketplace.cartitem.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findAllByUserId(Long userId);
}
