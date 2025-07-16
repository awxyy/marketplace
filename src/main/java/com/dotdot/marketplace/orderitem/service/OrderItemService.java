package com.dotdot.marketplace.orderitem.service;


import com.dotdot.marketplace.orderitem.dto.OrderItemRequestDto;
import com.dotdot.marketplace.orderitem.dto.OrderItemResponseDto;


public interface OrderItemService {

    OrderItemResponseDto createOrderItem(OrderItemRequestDto dto);

    OrderItemResponseDto getOrderItem(Long id);

    OrderItemResponseDto updateQuantity(Long id, int quantity);

    void deleteOrderItem(Long id);
}
