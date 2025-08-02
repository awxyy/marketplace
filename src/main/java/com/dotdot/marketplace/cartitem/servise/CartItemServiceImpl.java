package com.dotdot.marketplace.cartitem.servise;

import com.dotdot.marketplace.cartitem.dto.CartItemRequestDto;
import com.dotdot.marketplace.cartitem.dto.CartItemResponseDto;
import com.dotdot.marketplace.cartitem.entity.CartItem;
import com.dotdot.marketplace.cartitem.repository.CartItemRepository;
import com.dotdot.marketplace.exception.CartItemNotFoundException;
import com.dotdot.marketplace.exception.ProductNotFoundException;
import com.dotdot.marketplace.exception.UserNotFoundException;
import com.dotdot.marketplace.product.entity.Product;
import com.dotdot.marketplace.product.repository.ProductRepository;
import com.dotdot.marketplace.user.entity.User;
import com.dotdot.marketplace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private  final ProductRepository productRepository;

    @Override
    @CacheEvict(value = "userCartItems", key = "#dto.userId")
    public CartItemResponseDto addProductToCart(CartItemRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("User not found"));

        CartItem cartItem = new CartItem();
        cartItem.setUser(user);
        cartItem.setProduct(product);
        cartItem.setQuantity(dto.getQuantity());
        cartItem.setAddedAt(LocalDateTime.now());

        CartItem saved = cartItemRepository.save(cartItem);
        return modelMapper.map(saved, CartItemResponseDto.class);
    }

    @Override
    public CartItemResponseDto getCartItemById(Long id) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new CartItemNotFoundException("CartItem not found"));
        return modelMapper.map(cartItem, CartItemResponseDto.class);
    }

    @Override
    @Cacheable(value = "userCartItems", key = "#userId")
    public List<CartItemResponseDto> getAllCartItemByUserId(long userId) {
        List<CartItem> cartItems = cartItemRepository.findAllByUserId(userId);

        return cartItems.stream()
                .map(cartItem -> modelMapper.map(cartItem, CartItemResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = "userCartItems", key = "#dto.userId")
    public CartItemResponseDto updateQuantityByCartItemId(CartItemRequestDto dto, long id) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new CartItemNotFoundException("CartItem not found"));
        cartItem.setQuantity(dto.getQuantity());
        CartItem saved = cartItemRepository.save(cartItem);
        return modelMapper.map(saved, CartItemResponseDto.class);
    }


    @Override
    @CacheEvict(value = "userCartItems", key = "#userId")
    public void deleteCartItemById(Long id, Long userId) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new CartItemNotFoundException("CartItem not found"));
        if (cartItem.getUser().getId() != userId) {
            throw new IllegalArgumentException("CartItem does not belong to this user");
        }
        cartItemRepository.deleteById(id);
    }


}