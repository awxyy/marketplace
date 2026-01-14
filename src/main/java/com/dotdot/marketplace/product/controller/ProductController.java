package com.dotdot.marketplace.product.controller;

import com.dotdot.marketplace.product.dto.ProductFilterRequest;
import com.dotdot.marketplace.product.dto.ProductRequestDto;
import com.dotdot.marketplace.product.dto.ProductResponseDto;
import com.dotdot.marketplace.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDto> create(@Valid @RequestBody ProductRequestDto request) {
        log.info("Create product: {}", request.getName());
        ProductResponseDto response = productService.create(request);
        log.info("Product created with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAll() {
        log.info("Get all products");
        List<ProductResponseDto> response = productService.getAll();
        log.info("Product list size: {}", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<ProductResponseDto>> filter(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        log.info("Filter products");
        return ResponseEntity.ok(
            productService.filterProducts(
                ProductFilterRequest.of(name, minPrice, maxPrice, page, size, sortBy, direction)
            )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getById(@PathVariable long id) {
        log.info("Get product by ID: {}", id);
        ProductResponseDto response = productService.getById(id);
        log.info("Product retrieved by ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> update(@PathVariable long id, @Valid @RequestBody ProductRequestDto request) {
        log.info("Update product: {}", id);
        ProductResponseDto response = productService.update(id, request);
        log.info("Product updated with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        log.info("Delete product: {}", id);
        productService.delete(id);
        log.info("Product deleted with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
