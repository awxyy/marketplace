package com.dotdot.marketplace.user.controller;

import com.dotdot.marketplace.user.entity.UserRole;
import com.dotdot.marketplace.user.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService userRoleService;

    // 1. Отримати всі ролі користувача
    @GetMapping("/{userId}/roles")
    public ResponseEntity<Set<UserRole>> getUserRoles(@PathVariable Long userId) {
        Set<UserRole> roles = userRoleService.getUserRoles(userId);
        return ResponseEntity.ok(roles);
    }

    // 2. Перевірити чи має користувач конкретну роль
    @GetMapping("/{userId}/roles/{role}")
    public ResponseEntity<Boolean> hasRole(@PathVariable Long userId, @PathVariable UserRole role) {
        boolean hasRole = userRoleService.userHasRole(userId, role);
        return ResponseEntity.ok(hasRole);
    }

    // 3. Додати роль користувачу
    @PostMapping("/{userId}/roles/{role}")
    public ResponseEntity<String> addRole(@PathVariable Long userId, @PathVariable UserRole role) {
        userRoleService.addRoleToUser(userId, role);
        return ResponseEntity.ok("Role " + role + " added to user " + userId);
    }

    // 4. Видалити роль у користувача
    @DeleteMapping("/{userId}/roles/{role}")
    public ResponseEntity<String> removeRole(@PathVariable Long userId, @PathVariable UserRole role) {
        userRoleService.removeRoleFromUser(userId, role);
        return ResponseEntity.ok("Role " + role + " removed from user " + userId);
    }

    // 5. Створити користувача з ролями
    @PostMapping("/create-with-roles")
    public ResponseEntity<String> createUserWithRoles(@RequestBody CreateUserRequest request) {
        userRoleService.createUserWithRoles(
            request.getLogin(),
            request.getPassword(),
            request.getFullName(),
            request.getRoles()
        );
        return ResponseEntity.ok("User created with roles: " + request.getRoles());
    }

    // DTO для створення користувача
    public static class CreateUserRequest {
        private String login;
        private String password;
        private String fullName;
        private Set<UserRole> roles;

        // Getters and Setters
        public String getLogin() { return login; }
        public void setLogin(String login) { this.login = login; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public Set<UserRole> getRoles() { return roles; }
        public void setRoles(Set<UserRole> roles) { this.roles = roles; }
    }
}
