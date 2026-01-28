package com.dotdot.marketplace.productImage.service;

import com.dotdot.marketplace.configuration.minio.MinioService;
import com.dotdot.marketplace.product.entity.Product;
import com.dotdot.marketplace.product.repository.ProductRepository;
import com.dotdot.marketplace.productImage.entity.ProductImage;
import com.dotdot.marketplace.productImage.repository.ProductImageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductRepository productRepository;
    private final ProductImageRepo productImageRepo;
    private final MinioService minioService;

    @Transactional
    public ProductImage uploadImage(Long productId, MultipartFile file, boolean isMainImage) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        if (isMainImage) {
            for (ProductImage img : product.getImages()) {
                if (img.isMainImage()) {
                    img.setMainImage(false);
                    productImageRepo.save(img);
                }
            }
        }

        String fileName = minioService.uploadFile(file);

        ProductImage image = ProductImage.builder()
                .product(product)
                .url(fileName)
                .fileKey(fileName)
                .isMainImage(isMainImage)
                .contentType(file.getContentType())
                .build();

        return productImageRepo.save(image);
    }
}
