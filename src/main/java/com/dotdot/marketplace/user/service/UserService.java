package com.dotdot.marketplace.user.service;

import com.dotdot.marketplace.user.dto.UserRequestDto;
import com.dotdot.marketplace.user.dto.UserResponseDto;
import org.springframework.http.ResponseEntity;

public interface UserService {
    UserResponseDto createUser(UserRequestDto userRequest);
    UserResponseDto getUserById(Long id);
    UserResponseDto updateUser(Long id, UserRequestDto userRequest);
    ResponseEntity<Void> deleteUser(Long id);
}
