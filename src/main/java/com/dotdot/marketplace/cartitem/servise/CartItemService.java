package com.dotdot.marketplace.cartitem.servise;

import com.dotdot.marketplace.cartitem.dto.CartItemResponseDto;
import com.dotdot.marketplace.cartitem.dto.CartItemRequestDto;

import java.util.List;

public interface CartItemService {
    CartItemResponseDto addProductToCart(CartItemRequestDto dto);
    CartItemResponseDto getCartItemById(Long id);
    List<CartItemResponseDto> getAllCartItemByUserId(long userId);
    CartItemResponseDto updateQuantityByCartItemId(CartItemRequestDto dto, long id);
    void deleteCartItemById(Long id, Long userId);
}
