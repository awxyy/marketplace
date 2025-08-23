package com.dotdot.marketplace.user.service;

import com.dotdot.marketplace.user.dto.UserRequestDto;
import com.dotdot.marketplace.user.dto.UserResponseDto;
import com.dotdot.marketplace.user.entity.User;

public interface UserService {
    UserResponseDto createUser(UserRequestDto userRequest);

    UserResponseDto getUserById(Long id);

    UserResponseDto updateUser(Long id, UserRequestDto userRequest);

    void deleteUser(Long id);

    User findByLogin(String login);
}
