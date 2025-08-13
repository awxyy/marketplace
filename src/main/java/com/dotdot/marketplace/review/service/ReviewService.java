package com.dotdot.marketplace.review.service;

import com.dotdot.marketplace.review.dto.ReviewRequestDto;
import com.dotdot.marketplace.review.dto.ReviewResponseDto;

import java.util.List;

public interface ReviewService {
    ReviewResponseDto addReview(ReviewRequestDto reviewRequestDto);
    List<ReviewResponseDto> getReviewsByProductId(long productId);
    ReviewResponseDto getReviewsById(long reviewId);
    ReviewResponseDto updateReview(ReviewRequestDto reviewRequestDto,long id);
    void deleteReview(long reviewId);
    double getAverageRating(long productId);
    long getReviewCount(long productId);
}
