package com.dotdot.marketplace.order.dto;

import com.dotdot.marketplace.order.entity.OrderStatus;
import com.dotdot.marketplace.orderitem.dto.OrderItemRequestDto;

import java.util.List;

public class OrderRequestDtoBuilder {
    private OrderStatus status;
    private long user;
    private List<OrderItemRequestDto> orderItems;

    public OrderRequestDtoBuilder withStatus(OrderStatus status) {
        this.status = status;
        return this;
    }

    public OrderRequestDtoBuilder withUser(long user) {
        this.user = user;
        return this;
    }

    public OrderRequestDtoBuilder withOrderItems(List<OrderItemRequestDto> orderItems) {
        this.orderItems = orderItems;
        return this;
    }

    public OrderRequestDto build() {
        return new OrderRequestDto(status, user, orderItems);
    }
}
