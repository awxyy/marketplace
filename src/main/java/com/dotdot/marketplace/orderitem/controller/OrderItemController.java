package com.dotdot.marketplace.orderitem.controller;

import com.dotdot.marketplace.orderitem.dto.OrderItemRequestDto;
import com.dotdot.marketplace.orderitem.dto.OrderItemResponseDto;
import com.dotdot.marketplace.orderitem.service.OrderItemService;
import com.dotdot.marketplace.orderitem.service.OrderItemServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @PostMapping
    public ResponseEntity<OrderItemResponseDto> create(@RequestBody OrderItemRequestDto dto) {
        log.info("Creating order item with data: {}", dto.getOrderId());
        return ResponseEntity.status(HttpStatus.CREATED).body(orderItemService.createOrderItem(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemResponseDto> get(@PathVariable Long id) {
        log.info("Retrieving order item with id: {}", id);
        OrderItemResponseDto orderItemResponseDto = orderItemService.getOrderItem(id);
        log.info("Retrieved order item: {}", orderItemResponseDto);
        return ResponseEntity.ok(orderItemResponseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItemResponseDto> update(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        log.info("Updating order item with id: {}", id);
        int quantity = body.get("quantity");
        OrderItemResponseDto respons =  orderItemService.updateQuantity(id, quantity);
        log.info("Updated order item with id: {}", respons.getId());
        return ResponseEntity.ok(respons);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Deleting order item with id: {}", id);
        orderItemService.deleteOrderItem(id);
        log.info("Deleted order item with id: {}", id);
        return ResponseEntity.noContent().build();
    }
}
