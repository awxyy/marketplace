package com.dotdot.marketplace.user.controller;

import com.dotdot.marketplace.user.dto.UserRequestDto;
import com.dotdot.marketplace.user.dto.UserResponseDto;
import com.dotdot.marketplace.user.entity.User;
import com.dotdot.marketplace.user.entity.UserRole;
import com.dotdot.marketplace.user.service.UserRoleService;
import com.dotdot.marketplace.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserRoleService userRoleService;

    @PostMapping()
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequest) {
        log.info("Creating user");
        UserResponseDto createdUser = userService.createUser(userRequest);
        log.info("User created successfully with id: {}", createdUser.getId());
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable long id) {
        log.info("Getting user by id: {}", id);
        UserResponseDto user = userService.getUserById(id);
        log.info("User found successfully with id: {}", user.getId());
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable long id, @Valid @RequestBody UserRequestDto userRequest) {
        log.info("Updating user with id: {}", id);
        UserResponseDto updatedUser = userService.updateUser(id, userRequest);
        log.info("User updated successfully with id: {}", updatedUser.getId());
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        log.info("User deleted successfully with id: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Getting all users");
        List<User> users = userService.getAllUsers();
        log.info("All users retrieved successfully. Total count: {}", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}/roles")
    public ResponseEntity<Set<UserRole>> getUserRoles(@PathVariable Long userId) {
        log.info("Getting roles for user with id: {}", userId);
        Set<UserRole> userRoles = userRoleService.getUserRoles(userId);
        log.info("UserRoles retrieved successfully. Total count: {}", userRoles.size());
        return ResponseEntity.ok(userRoles);
    }

    @GetMapping("/{userId}/roles/{role}")
    public ResponseEntity<Boolean> hasRole(@PathVariable Long userId, @PathVariable UserRole role) {
        log.info("Checking if user with id: {} has role: {}", userId, role);
        Boolean hasRole = userRoleService.userHasRole(userId, role);
        log.info("User has role: {}", hasRole);
        return ResponseEntity.ok(hasRole);
    }

    @PostMapping("/{userId}/roles/{role}")
    public ResponseEntity<Void> addRole(@PathVariable Long userId, @PathVariable UserRole role) {
        log.info("Adding role: {} to user with id: {}", role, userId);
        userRoleService.addRoleToUser(userId, role);
        log.info("Role added successfully");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/roles/{role}")
    public ResponseEntity<Void> removeRole(@PathVariable Long userId, @PathVariable UserRole role) {
        log.info("Removing role: {} from user with id: {}", role, userId);
        userRoleService.removeRoleFromUser(userId, role);
        log.info("Role removed successfully");
        return ResponseEntity.noContent().build();
    }
}