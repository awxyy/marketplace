package com.dotdot.marketplace.user.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    private String login;
    private String fullName;

    @NotBlank(message = "Password cannot be empty")
    private String password;

}
