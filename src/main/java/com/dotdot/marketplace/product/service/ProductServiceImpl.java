package com.dotdot.marketplace.product.service;

import com.dotdot.marketplace.exception.UnauthorizedException;
import com.dotdot.marketplace.product.dto.ProductFilterRequest;
import com.dotdot.marketplace.product.dto.ProductRequestDto;
import com.dotdot.marketplace.product.dto.ProductResponseDto;
import com.dotdot.marketplace.product.entity.Product;
import com.dotdot.marketplace.product.entity.ProductStatus;
import com.dotdot.marketplace.product.repository.ProductRepository;
import com.dotdot.marketplace.product.spec.ProductSpecification;
import com.dotdot.marketplace.review.service.ReviewService;
import com.dotdot.marketplace.user.entity.User;
import com.dotdot.marketplace.user.entity.UserRole;
import com.dotdot.marketplace.user.repository.UserRepository;
import com.dotdot.marketplace.user.security.UserDetailsServiceImpl;
import com.dotdot.marketplace.user.security.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final UserDetailsServiceImpl userDetailsService;
    private final ReviewService reviewService;

    @Override
    @CacheEvict(value = "productList", allEntries = true)
    public ProductResponseDto create(ProductRequestDto request) {
        log.info("Creating product {}", request.getName());
        if (!userDetailsService.hasRole(UserRole.SELLER)) {
            log.warn("Unauthorized attempt to create product by non-seller user");
            throw new UnauthorizedException("Only users with SELLER status can create products");
        }

        User currentUser = userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> {
                    log.warn("User not found");
                    return new EntityNotFoundException("Current user not found");
                });

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSeller(currentUser);
        product.setCreatedAt(LocalDateTime.now());
        product.setStatus(ProductStatus.AVAILABLE);
        product.setQuantity(request.getQuantity());
        product.setReservedQuantity(0);
        Product savedProduct = productRepository.save(product);
        log.info("Saved product {}", savedProduct.getName());
        return modelMapper.map(savedProduct, ProductResponseDto.class);
    }

    @Override
    @Cacheable(value = "productDetails", key = "#id")
    public ProductResponseDto getById(long id) {
        log.info("Retrieving product {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found {}", id);
                    return new EntityNotFoundException("Product with ID " + id + " not found");
                });

        ProductResponseDto productResponseDto = modelMapper.map(product, ProductResponseDto.class);
        productResponseDto.setAverageRating(reviewService.getAverageRating(id));
        productResponseDto.setReviewCount(reviewService.getReviewCount(id));
        log.info("Retrieved product {}", id);
        return productResponseDto;
    }

    @Override
    @Cacheable(value = "productList")
    public List<ProductResponseDto> getAll() {
        log.info("Retrieving all products");
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
        log.info("Updating product {}", id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User currentUser = userPrincipal.getUser();

        if (!userDetailsService.hasRole(UserRole.SELLER)) {
            log.warn("Unauthorized attempt to update product by non-seller user");
            throw new UnauthorizedException("Only users with SELLER status can update products");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found {}", id);
                    return new EntityNotFoundException("Product with ID " + id + " not found");
                });

        if (product.getSeller().getId() !=  currentUser.getId()){
            log.warn("Unauthorized attempt to update product {} by user {}", id, currentUser.getId());
            throw new UnauthorizedException("You can only update your own products");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        Product updatedProduct = productRepository.save(product);
        log.info("Updated product {}", updatedProduct.getId());
        return modelMapper.map(updatedProduct, ProductResponseDto.class);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "productList", allEntries = true),
            @CacheEvict(value = "productDetails", key = "#id")
    })
    public void delete(long id) {
        log.info("Deleting product {}", id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User currentUser = userPrincipal.getUser();

        if (!userDetailsService.hasRole(UserRole.SELLER)) {
            log.warn("Unauthorized attempt to delete product by non-seller user");
            throw new UnauthorizedException("Only users with SELLER status can delete products");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found {}", id);
                    return new EntityNotFoundException("Product with ID " + id + " not found");
                });

        if (product.getSeller().getId() !=  currentUser.getId()) {
            log.warn("Unauthorized attempt to delete product {} by user {}", id, currentUser.getId());
            throw new UnauthorizedException("You can only delete your own products");
        }

        productRepository.delete(product);
        log.info("Deleted product {}", id);
    }

    public Page<ProductResponseDto> filterProducts(ProductFilterRequest filterRequest) {
        log.info("Filtering products");
        Specification<Product> spec = ProductSpecification.withFilters(filterRequest);

        Pageable pageable = PageRequest.of(
                filterRequest.getPage(),
                filterRequest.getSize(),
                Sort.by(Sort.Direction.fromString(filterRequest.getDirection()), filterRequest.getSortBy())
        );

        Page<Product> products = productRepository.findAll(spec, pageable);
        log.info("Found {} products", products.getTotalElements());
        return products.map(product -> {
            ProductResponseDto dto = modelMapper.map(product, ProductResponseDto.class);
            dto.setAverageRating(product.getAverageRating());
            dto.setReviewCount(product.getReviewsCount());
            return dto;
        });
    }

    private Long getCurrentUserId() {
        log.info("Getting current user ID");
        return userDetailsService.getCurrentUserId();
    }

}
