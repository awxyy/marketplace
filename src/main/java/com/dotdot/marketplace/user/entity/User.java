package com.dotdot.marketplace.user.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private long id;

    private String login;

    private String password;

    private String fullName;

    private String role;

    private LocalDateTime createdAt;
}
