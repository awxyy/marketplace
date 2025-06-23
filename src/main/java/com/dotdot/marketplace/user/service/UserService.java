package com.dotdot.marketplace.user.service;

import com.dotdot.marketplace.user.dto.UserRequestDto;
import com.dotdot.marketplace.user.dto.UserResponseDto;

public interface UserService {
    UserResponseDto createUser(UserRequestDto userRequest);
    UserResponseDto getUserById(Long id);
    UserResponseDto updateUser(Long id, UserRequestDto userRequest);
    void deleteUser(Long id);
}
