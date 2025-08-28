package com.dotdot.marketplace.review.controller;
import com.dotdot.marketplace.review.dto.ReviewRequestDto;
import com.dotdot.marketplace.review.dto.ReviewResponseDto;
import com.dotdot.marketplace.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponseDto> addReview(@RequestBody @Valid ReviewRequestDto request) {
        return ResponseEntity.ok(reviewService.addReview(request));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByProductId(@PathVariable long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProductId(productId));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDto> updateReview(@PathVariable long reviewId, @RequestBody @Valid ReviewRequestDto request) {
        return ResponseEntity.ok(reviewService.updateReview(request,reviewId));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("stats/{productId}")
    public ResponseEntity<Map<String, Object>> getReviewStats(@PathVariable long productId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("averageRating", reviewService.getAverageRating(productId));
        stats.put("reviewCount", reviewService.getReviewCount(productId));
        return ResponseEntity.ok(stats);
    }
}

