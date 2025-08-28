package com.dotdot.marketplace.user.service;

import com.dotdot.marketplace.exception.UserNotFoundException;
import com.dotdot.marketplace.user.dto.UserRequestDto;
import com.dotdot.marketplace.user.dto.UserResponseDto;
import com.dotdot.marketplace.user.entity.User;
import com.dotdot.marketplace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserResponseDto createUser(UserRequestDto userRequest) {
        validatePassword(userRequest.getPassword());
        User user = modelMapper.map(userRequest, User.class);
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, UserResponseDto.class);
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return modelMapper.map(user, UserResponseDto.class);
    }

    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        validatePassword(userRequest.getPassword());
        modelMapper.map(userRequest, user);
        User savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, UserResponseDto.class);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password must not be empty");
        }
    }

    @Override
    public User findByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException("User not found with login: " + login));

    }
}
