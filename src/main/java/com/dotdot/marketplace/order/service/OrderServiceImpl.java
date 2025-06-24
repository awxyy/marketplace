package com.dotdot.marketplace.order.service;


import com.dotdot.marketplace.exception.OrderNotFoundException;
import com.dotdot.marketplace.exception.UserNotFoundException;
import com.dotdot.marketplace.order.dto.OrderRequestDto;
import com.dotdot.marketplace.order.dto.OrderResponseDto;
import com.dotdot.marketplace.order.entity.Order;
import com.dotdot.marketplace.order.entity.OrderStatus;
import com.dotdot.marketplace.order.mapper.OrderMapper;
import com.dotdot.marketplace.order.repository.OrderRepository;
import com.dotdot.marketplace.orderitem.entity.OrderItem;
import com.dotdot.marketplace.user.entity.User;
import com.dotdot.marketplace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public OrderResponseDto createOrder(OrderRequestDto dto) {
        validateStatus(dto.getStatus());
        User user = userRepository.findById(dto.getUser())
                .orElseThrow(() -> new UserNotFoundException("User not found "));

        Order order = OrderMapper.mapOrderToEntity(dto, user);
        order.setTotalPrice(calculateTotalPrice(dto.getOrderItems()));
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        return modelMapper.map(savedOrder, OrderResponseDto.class);
    }

    @Override
    public OrderResponseDto getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
        return modelMapper.map(order, OrderResponseDto.class);
    }

    @Override
    public List<OrderResponseDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderResponseDto.class))
                .toList();
    }

    @Override
    public OrderResponseDto updateOrder(OrderRequestDto dto, long id) {
        validateStatus(dto.getStatus());
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        Order order = OrderMapper.updateExistingOrder(dto, existingOrder);
        order.setTotalPrice(calculateTotalPrice(dto.getOrderItems()));

        Order savedOrder = orderRepository.save(order);
        return modelMapper.map(savedOrder, OrderResponseDto.class);
    }

    @Override
    public void deleteOrderById(Long id) {
        orderRepository.deleteById(id);
    }

    private double calculateTotalPrice(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Total price must be non-negative");
        }
        return items.stream()
                .mapToDouble(item -> item.getPriceAtPurchase() * item.getQuantity())
                .sum();
    }

    private void validateStatus(OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status must not be null");
        }

        boolean valid = false;
        for (OrderStatus s : OrderStatus.values()) {
            if (s == status) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }
    }

}
