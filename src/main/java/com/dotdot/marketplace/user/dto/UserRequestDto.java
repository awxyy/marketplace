package com.dotdot.marketplace.user.dto;


import com.dotdot.marketplace.user.entity.UserRole;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {
    private String login;
    private String fullName;
    private UserRole role;
    private String password;
}