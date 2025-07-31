package com.dotdot.marketplace.product.service;

import com.dotdot.marketplace.product.dto.ProductRequestDto;
import com.dotdot.marketplace.product.dto.ProductResponseDto;
import com.dotdot.marketplace.product.entity.Product;
import com.dotdot.marketplace.product.entity.ProductStatus;
import com.dotdot.marketplace.product.repository.ProductRepository;
import com.dotdot.marketplace.user.entity.User;
import com.dotdot.marketplace.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @CacheEvict(value = "productList", allEntries = true)
    public ProductResponseDto create(ProductRequestDto request) {
        User seller = userRepository.findById(request.getSellerId())
                .orElseThrow(() -> new IllegalArgumentException("Seller with ID " + request.getSellerId() + " does not exist"));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSeller(seller);
        product.setCreatedAt(LocalDateTime.now());
        product.setStatus(ProductStatus.AVAILABLE);

        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductResponseDto.class);
    }

    @Override
    @Cacheable(value = "productDetails", key = "#id")
    public ProductResponseDto getById(long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + id + " not found"));

        return modelMapper.map(product, ProductResponseDto.class);
    }

    @Override
    @Cacheable(value = "productList")
    public List<ProductResponseDto> getAll() {
        return productRepository.findAll()
                .stream()
                .map(product -> modelMapper.map(product, ProductResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "productList", allEntries = true),
            @CacheEvict(value = "productDetails", key = "#id")
    })
    public ProductResponseDto update(long id, ProductRequestDto request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + id + " not found"));

        User seller = userRepository.findById(request.getSellerId())
                .orElseThrow(() -> new IllegalArgumentException("Seller with ID " + request.getSellerId() + " does not exist"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSeller(seller);
        Product updatedProduct = productRepository.save(product);

        return modelMapper.map(updatedProduct, ProductResponseDto.class);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "productList", allEntries = true),
            @CacheEvict(value = "productDetails", key = "#id")
    })
    public void delete(long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + id + " not found"));
        productRepository.delete(product);
    }

}
