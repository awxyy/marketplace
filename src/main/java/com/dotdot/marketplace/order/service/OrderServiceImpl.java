package com.dotdot.marketplace.order.service;


import com.dotdot.marketplace.exception.OrderNotFoundException;
import com.dotdot.marketplace.exception.ProductUnavailableException;
import com.dotdot.marketplace.exception.ReservationConversionException;
import com.dotdot.marketplace.order.dto.OrderRequestDto;
import com.dotdot.marketplace.order.dto.OrderResponseDto;
import com.dotdot.marketplace.order.entity.Order;
import com.dotdot.marketplace.order.entity.OrderStatus;
import com.dotdot.marketplace.order.repository.OrderRepository;
import com.dotdot.marketplace.orderitem.dto.OrderItemRequestDto;
import com.dotdot.marketplace.orderitem.entity.OrderItem;
import com.dotdot.marketplace.product.entity.Product;
import com.dotdot.marketplace.product.entity.ProductStatus;
import com.dotdot.marketplace.product.repository.ProductRepository;
import com.dotdot.marketplace.reservation.service.ReservationService;
import com.dotdot.marketplace.user.entity.User;
import com.dotdot.marketplace.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final ReservationService reservationService;

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto dto) {
        User user = userRepository.findById(dto.getUser())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Order order = new Order();
        order.setStatus(dto.getStatus());
        order.setCreatedAt(LocalDateTime.now());
        order.setUser(user);

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequestDto itemDto : dto.getOrderItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            validateProductStatus(product);

            if (itemDto.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }

            if (product.getAvailableQuantity() < itemDto.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            try {
                reservationService.convertReservationToOrder(dto.getUser(), itemDto.getProductId());
            } catch (ReservationConversionException e) {
                if (product.getQuantity() < itemDto.getQuantity()) {
                    throw new RuntimeException("Not enough stock for product: " + product.getName());
                }
                product.setQuantity(product.getQuantity() - itemDto.getQuantity());
                productRepository.save(product);
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setPriceAtPurchase(product.getPrice());

            orderItems.add(orderItem);
        }

        if (orderItems.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        order.setOrderItems(orderItems);

        double totalPrice = calculateTotalPrice(orderItems);
        order.setTotalPrice(totalPrice);
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
        existingOrder.setStatus(dto.getStatus());
        existingOrder.getOrderItems().clear();
        for (OrderItemRequestDto itemDto : dto.getOrderItems()) {
            OrderItem orderItem = new OrderItem();

            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with id " + itemDto.getProductId()));

            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setOrder(existingOrder);

            existingOrder.getOrderItems().add(orderItem);
        }

        existingOrder.setTotalPrice(calculateTotalPrice(existingOrder.getOrderItems()));

        Order savedOrder = orderRepository.save(existingOrder);
        return modelMapper.map(savedOrder, OrderResponseDto.class);
    }

    @Override
    public void deleteOrderById(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }

    public double calculateTotalPrice(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        return items.stream()
                .mapToDouble(item -> item.getPriceAtPurchase() * item.getQuantity())
                .sum();
    }

    public void validateStatus(OrderStatus status) {
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

    private void validateProductStatus(Product product) {
        if (product.getStatus() != ProductStatus.AVAILABLE) {
            throw new ProductUnavailableException(
                    "Product is not available for purchase: " + product.getName()
            );
        }
    }

}
