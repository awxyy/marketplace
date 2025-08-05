package com.dotdot.marketplace.order.dto;

import com.dotdot.marketplace.order.entity.OrderStatus;
import com.dotdot.marketplace.orderitem.dto.OrderItemRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {
    private OrderStatus status;
    private long user;
    private List<OrderItemRequestDto> orderItems;
}
