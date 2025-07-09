package com.dotdot.marketplace.cartitem.controller;

import com.dotdot.marketplace.cartitem.dto.CartItemRequestDto;
import com.dotdot.marketplace.cartitem.dto.CartItemResponseDto;
import com.dotdot.marketplace.cartitem.servise.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cart-items")
public class CartItemController {
    private final CartItemService cartItemService;

    @PostMapping
    public ResponseEntity<CartItemResponseDto> addProductToCart(@RequestBody CartItemRequestDto dto) {
        return ResponseEntity.ok(cartItemService.addProductToCart(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartItemResponseDto> getCartItemById(@PathVariable Long id) {
        return ResponseEntity.ok(cartItemService.getCartItemById(id));
    }

    @GetMapping("/user/{user_id}")
    public ResponseEntity<List<CartItemResponseDto>> getAllCartItemByUserId(@PathVariable Long user_id) {
        return ResponseEntity.ok(cartItemService.getAllCartItemByUserId(user_id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartItemResponseDto> updateCartItem(@PathVariable Long id, @RequestBody CartItemRequestDto dto) {
        return ResponseEntity.ok(cartItemService.changeQuantityByCartItemId(dto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long id) {
        cartItemService.deleteCartItemById(id);
        return ResponseEntity.noContent().build();
    }
}
