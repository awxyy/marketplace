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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private static final ProductStatus DEFAULT_STATUS = ProductStatus.AVAILABLE;

    @Override
    public ProductResponseDto create(ProductRequestDto request) {
        User seller = userRepository.findById(request.getSellerId()).orElseThrow(() -> new IllegalArgumentException("Seller with ID " + request.getSellerId() + " does not exist"));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSeller(seller);
        product.setStatus(DEFAULT_STATUS);
        product.setCreatedAt(LocalDateTime.now());

        Product saved = productRepository.save(product);

        return mapToResponse(saved);
    }

    @Override
    public ProductResponseDto getById(long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + id + " not found"));

        return mapToResponse(product);
    }

    @Override
    public List<ProductResponseDto> getAll() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDto update(long id, ProductRequestDto request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + id + " not found"));

        User seller = userRepository.findById(request.getSellerId())
                .orElseThrow(() -> new IllegalArgumentException("Seller with ID " + request.getSellerId() + " does not exist"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSeller(seller);
        // не змінюємо createdAt і status
        Product updated = productRepository.save(product);

        return mapToResponse(updated);
    }

    @Override
    public void delete(long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + id + " not found"));
        productRepository.delete(product);
    }

    private ProductResponseDto mapToResponse(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getSeller().getLogin(),
                product.getStatus().name(),
                product.getCreatedAt()
        );
    }
}
