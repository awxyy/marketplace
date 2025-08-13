package com.dotdot.marketplace.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
    private Long id;
    private Long productId;
    private Long userId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
