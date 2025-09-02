package com.dotdot.marketplace.user.controller;

import com.dotdot.marketplace.user.entity.UserRole;
import com.dotdot.marketplace.user.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final UserRoleService userRoleService;

    @PostMapping("/create-user")
    public ResponseEntity<String> createTestUser() {
        userRoleService.createUserWithRoles(
            "testuser",
            "$2a$10$YourHashedPasswordHere",
            "Test User",
            Set.of(UserRole.USER)
        );
        return ResponseEntity.ok("Test user created with USER role");
    }

    @PostMapping("/create-seller")
    public ResponseEntity<String> createTestSeller() {
        userRoleService.createUserWithRoles(
            "testseller",
            "$2a$10$YourHashedPasswordHere",
            "Test Seller",
            Set.of(UserRole.USER, UserRole.SELLER)
        );
        return ResponseEntity.ok("Test seller created with USER + SELLER roles");
    }
}
