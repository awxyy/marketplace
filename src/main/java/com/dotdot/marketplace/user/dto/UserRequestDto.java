package com.dotdot.marketplace.user.dto;


import com.dotdot.marketplace.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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