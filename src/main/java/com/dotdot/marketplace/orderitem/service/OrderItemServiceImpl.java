package com.dotdot.marketplace.orderitem.service;

import com.dotdot.marketplace.order.dto.OrderResponseDto;
import com.dotdot.marketplace.order.entity.Order;
import com.dotdot.marketplace.order.repository.OrderRepository;
import com.dotdot.marketplace.orderitem.dto.OrderItemRequestDto;
import com.dotdot.marketplace.orderitem.dto.OrderItemResponseDto;
import com.dotdot.marketplace.orderitem.entity.OrderItem;
import com.dotdot.marketplace.orderitem.repository.OrderItemRepository;
import com.dotdot.marketplace.product.entity.Product;
import com.dotdot.marketplace.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderItemResponseDto createOrderItem(OrderItemRequestDto dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order with ID " + dto.getOrderId() + " not found."));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + dto.getProductId() + " not found."));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(dto.getQuantity());
        orderItem.setPriceAtPurchase(product.getPrice());

        OrderItem savedOrderItem = orderItemRepository.save(orderItem);


        return modelMapper.map(savedOrderItem, OrderItemResponseDto.class);

    }

    @Override
    @Transactional(readOnly = true)
    public OrderItemResponseDto getOrderItem(Long id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order item with ID " + id + " not found."));
        return modelMapper.map(orderItem, OrderItemResponseDto.class);
    }

    @Override
    @Transactional
    public OrderItemResponseDto updateQuantity(Long id, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be a positive number.");
        }

        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order item with ID " + id + " not found."));

        orderItem.setQuantity(quantity);
        OrderItem updatedOrderItem = orderItemRepository.save(orderItem);
        return modelMapper.map(updatedOrderItem, OrderItemResponseDto.class);
    }

    @Override
    @Transactional
    public void deleteOrderItem(Long id) {
        if (!orderItemRepository.existsById(id)) {
            throw new EntityNotFoundException("Order item with ID " + id + " not found.");
        }
        orderItemRepository.deleteById(id);
    }


}