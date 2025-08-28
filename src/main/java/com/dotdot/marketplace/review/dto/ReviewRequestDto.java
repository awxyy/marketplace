package com.dotdot.marketplace.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto {
    private Long productId;

    private Long userId;

    private int rating;

    private String comment;
}
