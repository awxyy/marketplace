package com.dotdot.marketplace.order.service;


import com.dotdot.marketplace.order.dto.OrderRequestDto;
import com.dotdot.marketplace.order.dto.OrderResponseDto;

import java.util.List;


public interface OrderService {
    OrderResponseDto createOrder(OrderRequestDto dto);

    OrderResponseDto getOrderById(Long id);

    List<OrderResponseDto> getAllOrders();

    OrderResponseDto updateOrder(OrderRequestDto dto, long id);

    void deleteOrderById(Long id);
}
