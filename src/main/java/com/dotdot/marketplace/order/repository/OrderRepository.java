package com.dotdot.marketplace.order.repository;

import com.dotdot.marketplace.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
