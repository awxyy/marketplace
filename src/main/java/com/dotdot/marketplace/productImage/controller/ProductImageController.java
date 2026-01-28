package com.dotdot.marketplace.productImage.controller;

import com.dotdot.marketplace.productImage.entity.ProductImage;
import com.dotdot.marketplace.productImage.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product-images")
public class ProductImageController {
    private final ProductImageService productImageService;

    @PostMapping("/{id}/upload")
    public ResponseEntity<ProductImage> addProductImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isMainImage", defaultValue = "false") boolean isMainImage
    ) {
        ProductImage savedImage = productImageService.uploadImage(id, file, isMainImage);
        return ResponseEntity.ok(savedImage);
    }
}
