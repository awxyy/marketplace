package com.dotdot.marketplace.review.repository;

import com.dotdot.marketplace.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository  extends JpaRepository<Review, Long> {
    List<Review> findByProductId(long productId);
    long countByProductId(long productId);
}