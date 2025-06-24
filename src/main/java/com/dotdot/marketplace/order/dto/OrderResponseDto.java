package com.dotdot.marketplace.order.dto;

import com.dotdot.marketplace.order.entity.OrderStatus;
import com.dotdot.marketplace.orderitem.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
    private long id;
    private OrderStatus status;
    private double totalPrice;
    private LocalDateTime createdAt;
    private long userId;
    List<OrderItem> orderItems;
}
