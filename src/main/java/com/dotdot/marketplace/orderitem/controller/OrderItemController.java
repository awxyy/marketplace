package com.dotdot.marketplace.orderitem.controller;

import com.dotdot.marketplace.orderitem.dto.OrderItemRequestDto;
import com.dotdot.marketplace.orderitem.dto.OrderItemResponseDto;
import com.dotdot.marketplace.orderitem.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @PostMapping
    public ResponseEntity<OrderItemResponseDto> create(@RequestBody OrderItemRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderItemService.createOrderItem(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(orderItemService.getOrderItem(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItemResponseDto> update(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        int quantity = body.get("quantity");
        return ResponseEntity.ok(orderItemService.updateQuantity(id, quantity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.noContent().build();
    }
}
