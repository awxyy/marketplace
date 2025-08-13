package com.dotdot.marketplace.review.service;

import com.dotdot.marketplace.product.entity.Product;
import com.dotdot.marketplace.product.repository.ProductRepository;
import com.dotdot.marketplace.review.dto.ReviewRequestDto;
import com.dotdot.marketplace.review.dto.ReviewResponseDto;
import com.dotdot.marketplace.review.entity.Review;
import com.dotdot.marketplace.review.repository.ReviewRepository;
import com.dotdot.marketplace.user.entity.User;
import com.dotdot.marketplace.user.repository.UserRepository;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;

    @Override
    public ReviewResponseDto addReview(ReviewRequestDto reviewRequestDto) {
        if(reviewRequestDto.getRating() < 1 || reviewRequestDto.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Product product = productRepository.findById(reviewRequestDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: "));

        User user = userRepository.findById(reviewRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setComment(reviewRequestDto.getComment());
        review.setRating(reviewRequestDto.getRating());
        review.setCreatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);

        return modelMapper.map(savedReview, ReviewResponseDto.class);
    }

    @Override
    public List<ReviewResponseDto> getReviewsByProductId(long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Product not found");
        }
        List<Review> reviews = reviewRepository.findByProductId(productId);
        return reviews.stream()
                .map(review -> modelMapper.map(review, ReviewResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponseDto getReviewsById(long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        return modelMapper.map(review, ReviewResponseDto.class);
    }

    @Override
    public ReviewResponseDto updateReview(ReviewRequestDto reviewRequestDto,long reviewId) {
        if(reviewRequestDto.getRating() < 1 || reviewRequestDto.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        review.setComment(reviewRequestDto.getComment());
        review.setRating(reviewRequestDto.getRating());
        return modelMapper.map(reviewRepository.save(review), ReviewResponseDto.class);
    }

    @Override
    public void deleteReview(long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        reviewRepository.delete(review);

    }

    public double getAverageRating(long productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0);
    }

    public long getReviewCount(long productId) {
        return reviewRepository.countByProductId(productId);
    }
}
