package com.dotdot.marketplace.auth.dto;

import com.dotdot.marketplace.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String login;
    private String password;
    private String fullName;
    private Set<UserRole> roles;
}
