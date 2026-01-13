package com.dotdot.marketplace.review.controller;
import com.dotdot.marketplace.review.dto.ReviewRequestDto;
import com.dotdot.marketplace.review.dto.ReviewResponseDto;
import com.dotdot.marketplace.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponseDto> addReview(@RequestBody @Valid ReviewRequestDto request) {
        log.info("Adding review for product id: {} with rating: {}", request.getProductId(), request.getRating());

        ReviewResponseDto responseDto = reviewService.addReview(request);

        log.info("Review added successfully with id: {}", responseDto.getId());

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByProductId(@PathVariable long productId) {
        log.info("Fetching reviews for product with id: {}", productId);

        List<ReviewResponseDto> reviews = reviewService.getReviewsByProductId(productId);

        log.info("Reviews found for product {}: count = {}", productId, reviews.size());

        return ResponseEntity.ok(reviews);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDto> updateReview(@PathVariable long reviewId, @RequestBody @Valid ReviewRequestDto request) {
        log.info("Updating review for id: {} with rating: {}", reviewId, request.getRating());
        ReviewResponseDto updatedReview = reviewService.updateReview(request, reviewId);
        log.info("Review updated successfully with id: {}", updatedReview.getId());
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable long reviewId) {
        log.info("Deleting review with id: {}", reviewId);
        reviewService.deleteReview(reviewId);
        log.info("Review deleted successfully with id: {}", reviewId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("stats/{productId}")
    public ResponseEntity<Map<String, Object>> getReviewStats(@PathVariable long productId) {
        log.info("Fetching review stats for product with id: {}", productId);
        Map<String, Object> stats = new HashMap<>();
        stats.put("averageRating", reviewService.getAverageRating(productId));
        stats.put("reviewCount", reviewService.getReviewCount(productId));
        log.info("Review stats for product {}: {}", productId, stats);
        return ResponseEntity.ok(stats);
    }
}

