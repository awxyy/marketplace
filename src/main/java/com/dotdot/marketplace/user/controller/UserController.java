package com.dotdot.marketplace.user.controller;

import com.dotdot.marketplace.user.dto.UserRequestDto;
import com.dotdot.marketplace.user.dto.UserResponseDto;
import com.dotdot.marketplace.user.entity.User;
import com.dotdot.marketplace.user.entity.UserRole;
import com.dotdot.marketplace.user.service.UserRoleService;
import com.dotdot.marketplace.user.service.UserService;
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

    private final UserService userService;
    private final UserRoleService userRoleService;

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
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}/roles")
    public ResponseEntity<Set<UserRole>> getUserRoles(@PathVariable Long userId) {
        return ResponseEntity.ok(userRoleService.getUserRoles(userId));
    }

    @GetMapping("/{userId}/roles/{role}")
    public ResponseEntity<Boolean> hasRole(@PathVariable Long userId, @PathVariable UserRole role) {
        return ResponseEntity.ok(userRoleService.userHasRole(userId, role));
    }

    @PostMapping("/{userId}/roles/{role}")
    public ResponseEntity<Void> addRole(@PathVariable Long userId, @PathVariable UserRole role) {
        userRoleService.addRoleToUser(userId, role);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/roles/{role}")
    public ResponseEntity<Void> removeRole(@PathVariable Long userId, @PathVariable UserRole role) {
        userRoleService.removeRoleFromUser(userId, role);
        return ResponseEntity.noContent().build();
    }
}