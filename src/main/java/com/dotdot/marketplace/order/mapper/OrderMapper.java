package com.dotdot.marketplace.order.mapper;

import com.dotdot.marketplace.order.dto.OrderRequestDto;
import com.dotdot.marketplace.order.entity.Order;
import com.dotdot.marketplace.order.entity.OrderStatus;
import com.dotdot.marketplace.user.entity.User;


public class OrderMapper {

    public static Order mapOrderToEntity(OrderRequestDto dto, User user) {
        if (dto == null) return null;

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        return order;
    }

    public static Order updateExistingOrder(OrderRequestDto dto, Order existingOrder) {
        if (dto == null || existingOrder == null) return existingOrder;

        if (dto.getStatus() != null) {
            existingOrder.setStatus(dto.getStatus());
        }
        return existingOrder;
    }

}
