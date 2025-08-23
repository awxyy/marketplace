package com.dotdot.marketplace.user.controller;

import com.dotdot.marketplace.user.dto.UserRequestDto;
import com.dotdot.marketplace.user.dto.UserResponseDto;
import com.dotdot.marketplace.user.entity.User;
import com.dotdot.marketplace.user.entity.UserRole;
import com.dotdot.marketplace.user.repository.UserRepository;
import com.dotdot.marketplace.user.service.UserRoleService;
import com.dotdot.marketplace.user.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserServiceImpl userService;
    private final UserRoleService userRoleService;
    private final UserRepository userRepository;

    @PostMapping()
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequest) {
        return ResponseEntity.ok(userService.createUser(userRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable long id, @Valid @RequestBody UserRequestDto userRequest) {
        return ResponseEntity.ok(userService.updateUser(id, userRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}/roles")
    public ResponseEntity<Set<UserRole>> getUserRoles(@PathVariable Long userId) {
        Set<UserRole> roles = userRoleService.getUserRoles(userId);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{userId}/roles/{role}")
    public ResponseEntity<Boolean> hasRole(@PathVariable Long userId, @PathVariable UserRole role) {
        boolean hasRole = userRoleService.userHasRole(userId, role);
        return ResponseEntity.ok(hasRole);
    }

    @PostMapping("/{userId}/roles/{role}")
    public ResponseEntity<String> addRole(@PathVariable Long userId, @PathVariable UserRole role) {
        userRoleService.addRoleToUser(userId, role);
        return ResponseEntity.ok("Role " + role + " added to user " + userId);
    }

    @DeleteMapping("/{userId}/roles/{role}")
    public ResponseEntity<String> removeRole(@PathVariable Long userId, @PathVariable UserRole role) {
        userRoleService.removeRoleFromUser(userId, role);
        return ResponseEntity.ok("Role " + role + " removed from user " + userId);
    }

}
