package com.dotdot.marketplace.user.service;

import com.dotdot.marketplace.exception.UserNotFoundException;
import com.dotdot.marketplace.user.dto.UserRequestDto;
import com.dotdot.marketplace.user.dto.UserResponseDto;
import com.dotdot.marketplace.user.entity.User;
import com.dotdot.marketplace.user.entity.UserRole;
import com.dotdot.marketplace.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;


@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserResponseDto mapToUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getLogin(),
                user.getFullName(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    public UserResponseDto createUser(UserRequestDto userRequest) {
        User user = new User();
        user.setLogin(userRequest.getLogin());
        user.setPassword(userRequest.getPassword());
// TODO: Hash password before saving (e.g., BCryptPasswordEncoder)
        user.setFullName(userRequest.getFullName());
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(UserRole.USER);
        User savedUser = userRepository.save(user);

        return mapToUserResponseDto(savedUser);
    }

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return mapToUserResponseDto(user);
    }

    public UserResponseDto updateUser(Long id, UserRequestDto userRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        user.setLogin(userRequest.getLogin());
        user.setFullName(userRequest.getFullName());
        user.setPassword(userRequest.getPassword());

        userRepository.save(user);

        return  mapToUserResponseDto(user);
    }

    public ResponseEntity<Void> deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
