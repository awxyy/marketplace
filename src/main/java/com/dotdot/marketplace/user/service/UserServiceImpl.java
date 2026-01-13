package com.dotdot.marketplace.user.service;

import com.dotdot.marketplace.exception.UserNotFoundException;
import com.dotdot.marketplace.user.dto.UserRequestDto;
import com.dotdot.marketplace.user.dto.UserResponseDto;
import com.dotdot.marketplace.user.entity.User;
import com.dotdot.marketplace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserResponseDto createUser(UserRequestDto userRequest) {
        if (userRepository.existsByLogin(userRequest.getLogin())) {
            log.warn("Registration attempt failed: Login '{}' already taken", userRequest.getLogin());
            throw new RuntimeException("User with this login already exists");
        }
        validatePassword(userRequest.getPassword());

        User user = modelMapper.map(userRequest, User.class);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        log.info("User created with id: {}", savedUser.getId());
        return modelMapper.map(savedUser, UserResponseDto.class);
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        log.info("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new UserNotFoundException("User not found with id: " + id);
                });
        log.info("User found: {}", user.getId());
        return modelMapper.map(user, UserResponseDto.class);
    }

    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto userRequest) {
        log.info("Updating user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new UserNotFoundException("User not found with id: " + id);
                });

        if (!user.getLogin().equals(userRequest.getLogin())
                && userRepository.existsByLogin(userRequest.getLogin())) {

            log.warn("Update failed: Login '{}' already taken by another user", userRequest.getLogin());
            throw new RuntimeException( "Login already taken");
        }

        validatePassword(userRequest.getPassword());
        log.info("Password validation passed for login: {}", userRequest.getLogin());
        modelMapper.map(userRequest, user);
        User savedUser = userRepository.save(user);
        log.info("User updated with id: {}", savedUser.getId());
        return modelMapper.map(savedUser, UserResponseDto.class);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            log.warn("User not found with id: {}", id);
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        log.info("User deleted with id: {}", id);
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Fetching all users");
        List<User> users = userRepository.findAll();
        return users;
    }

    @Override
    public User findByLogin(String login) {
        log.info("Finding user by login: {}", login);
        return userRepository.findByLogin(login)
                .orElseThrow(() -> {
                    log.warn("User not found with login: {}", login);
                    return new UserNotFoundException("User not found with login: " + login);
                });
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            log.warn("Password is blank");
            throw new IllegalArgumentException("Password must not be empty");
        }
        log.info("Password validation passed");
    }
}