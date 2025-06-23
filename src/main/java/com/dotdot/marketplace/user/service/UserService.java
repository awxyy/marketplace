package com.dotdot.marketplace.user.service;

import com.dotdot.marketplace.user.dto.UserRequest;
import com.dotdot.marketplace.user.dto.UserResponse;
import com.dotdot.marketplace.user.entity.User;
import com.dotdot.marketplace.user.entity.UserRole;
import com.dotdot.marketplace.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(UserRequest userRequest) {
        User user = new User();
        user.setLogin(userRequest.getLogin());
        user.setPassword(userRequest.getPassword());
        user.setFullName(userRequest.getFullName());
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(UserRole.USER);
        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getLogin(),
                savedUser.getFullName(),
                savedUser.getRole(),
                savedUser.getCreatedAt()
        );
    }


    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));

        return new UserResponse(
                user.getId(),
                user.getLogin(),
                user.getFullName(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));

        user.setLogin(userRequest.getLogin());
        user.setFullName(userRequest.getFullName());
        user.setPassword(userRequest.getPassword());

        userRepository.save(user);

        return new UserResponse(
                user.getId(),
                user.getLogin(),
                user.getFullName(),
                user.getRole(),
                user.getCreatedAt()
        );
    }



    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));
        userRepository.delete(user);

    }



}
