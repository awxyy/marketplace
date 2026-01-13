package com.dotdot.marketplace.order.controller;


import com.dotdot.marketplace.order.dto.OrderRequestDto;
import com.dotdot.marketplace.order.dto.OrderResponseDto;
import com.dotdot.marketplace.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto orderRequestDto) {
        log.info("Creating order");
        OrderResponseDto orderResponseDto = orderService.createOrder(orderRequestDto);
        log.info("Order created: {}", orderResponseDto.getId());
        return ResponseEntity.ok(orderResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable long id) {
        log.info("Get order by id: {}", id);
        OrderResponseDto orderResponseDto = orderService.getOrderById(id);
        log.info("Order retrieved: {}", id);
        return ResponseEntity.ok(orderResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAll() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDto> updateOrder(@RequestBody OrderRequestDto orderRequestDto, @PathVariable Long id) {
        return ResponseEntity.ok(orderService.updateOrder(orderRequestDto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable long id) {
        orderService.deleteOrderById(id);
        return ResponseEntity.noContent().build();
    }

}
