package com.dotdot.marketplace.review.service;

import com.dotdot.marketplace.mongo.service.LogService;
import com.dotdot.marketplace.product.entity.Product;
import com.dotdot.marketplace.product.repository.ProductRepository;
import com.dotdot.marketplace.review.dto.ReviewRequestDto;
import com.dotdot.marketplace.review.dto.ReviewResponseDto;
import com.dotdot.marketplace.review.entity.Review;
import com.dotdot.marketplace.review.repository.ReviewRepository;
import com.dotdot.marketplace.user.entity.User;
import com.dotdot.marketplace.user.repository.UserRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;
    private final LogService logService;

    private static final String LOG_SOURCE = "ReviewServiceImpl";

    @Override
    public ReviewResponseDto addReview(ReviewRequestDto reviewRequestDto) {
        log.info("Adding review: {}", reviewRequestDto.getUserId());
        if(reviewRequestDto.getRating() < 1 || reviewRequestDto.getRating() > 5) {
            String errorMsg = "Invalid rating value: " + reviewRequestDto.getRating();
            logService.error(LOG_SOURCE, errorMsg, new IllegalArgumentException(errorMsg));
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Product product = productRepository.findById(reviewRequestDto.getProductId())
                .orElseThrow(() ->{
                    String errorMsg = "Product not found: " + reviewRequestDto.getProductId();
                    logService.error(LOG_SOURCE, errorMsg, new IllegalArgumentException(errorMsg));
                 return new IllegalArgumentException("Product not found with ID: "+ reviewRequestDto.getProductId());
                });

        User user = userRepository.findById(reviewRequestDto.getUserId())
                .orElseThrow(() -> {
                    String errorMsg = "User not found: " + reviewRequestDto.getUserId();
                    logService.error(LOG_SOURCE, errorMsg, new IllegalArgumentException(errorMsg));
                    return new IllegalArgumentException("User not found");
                });

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setComment(reviewRequestDto.getComment());
        review.setRating(reviewRequestDto.getRating());
        review.setCreatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);
        Map<String, Object> logData = Map.of(
                "reviewId", savedReview.getId(),
                "productId", product.getId(),
                "userId", user.getId()
        );
        logService.info(LOG_SOURCE, "Review added successfully", logData);
        updateProductRating(product);

        return modelMapper.map(savedReview, ReviewResponseDto.class);
    }

    @Override
    public List<ReviewResponseDto> getReviewsByProductId(long productId) {
        log.info("Get reviews by product ID: {}", productId);
        if (!productRepository.existsById(productId)) {
            log.warn("Product not found: {}", productId);
            throw new IllegalArgumentException("Product not found");
        }
        List<Review> reviews = reviewRepository.findByProductId(productId);
        return reviews.stream()
                .map(review -> modelMapper.map(review, ReviewResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponseDto getReviewsById(long reviewId) {
        log.info("Get review by ID: {}", reviewId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    log.warn("Review not found: {}", reviewId);
                    return new IllegalArgumentException("Review not found");
                });
        log.info("Review found successfully: ID {}", review.getId());
        return modelMapper.map(review, ReviewResponseDto.class);
    }

    @Override
    public ReviewResponseDto updateReview(ReviewRequestDto reviewRequestDto, long reviewId) {
        log.info("Updating review: {}", reviewId);
        if(reviewRequestDto.getRating() < 1 || reviewRequestDto.getRating() > 5) {
            log.warn("Invalid rating value: {}", reviewRequestDto.getRating());
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    log.warn("Review not found: {}", reviewId);
                    return new IllegalArgumentException("Review not found");
                });
        review.setComment(reviewRequestDto.getComment());
        review.setRating(reviewRequestDto.getRating());

        Review updatedReview = reviewRepository.save(review);
        updateProductRating(updatedReview.getProduct());
        log.info("Review updated successfully: {}", updatedReview.getId());
        return modelMapper.map(updatedReview, ReviewResponseDto.class);
    }

    @Override
    public void deleteReview(long reviewId) {
        log.info("Deleting review: {}", reviewId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        Product product = review.getProduct();
        reviewRepository.delete(review);
        updateProductRating(product);
        log.info("Review deleted successfully: {}", reviewId);
    }

    public double getAverageRating(long productId) {
        log.info("Get average rating: {}", productId);
        List<Review> reviews = reviewRepository.findByProductId(productId);
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0);
    }

    public long getReviewCount(long productId) {
        log.info("Get review count: {}", productId);
        return reviewRepository.countByProductId(productId);
    }

    private void updateProductRating(Product product) {
        log.info("Updating product rating: {}", product.getId());
        List<Review> reviews = reviewRepository.findByProductId(product.getId());

        double avgRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        product.setAverageRating(avgRating);
        product.setReviewsCount((long) reviews.size());
        productRepository.save(product);
        log.info("Product rating updated successfully: {}", product.getId());
    }

}