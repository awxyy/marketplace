package com.dotdot.marketplace.user.dto;


import com.dotdot.marketplace.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private long id;
    private String login;
    private String fullName;
    private UserRole role;
    private LocalDateTime createdAt;
}
