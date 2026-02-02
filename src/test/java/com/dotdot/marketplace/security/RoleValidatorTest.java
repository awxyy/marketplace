package com.dotdot.marketplace.security;

import com.dotdot.marketplace.configuration.security.RoleValidator;
import com.dotdot.marketplace.exception.UnauthorizedException;
import com.dotdot.marketplace.user.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoleValidatorTest {

    private RoleValidator roleValidator;

    @BeforeEach
    void setUp() {
        roleValidator = new RoleValidator();
        // Clear security context before each test
        SecurityContextHolder.clearContext();
    }

    @Test
    void validateRole_withValidSellerRole_shouldPass() {
        // Arrange
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                1L, null, List.of(new SimpleGrantedAuthority("ROLE_SELLER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act & Assert
        assertDoesNotThrow(() -> roleValidator.validateRole(UserRole.SELLER));
    }

    @Test
    void validateRole_withValidUserRole_shouldPass() {
        // Arrange
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                1L, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act & Assert
        assertDoesNotThrow(() -> roleValidator.validateRole(UserRole.USER));
    }

    @Test
    void validateRole_withInvalidRole_shouldThrowUnauthorizedException() {
        // Arrange
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                1L, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act & Assert
        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> roleValidator.validateRole(UserRole.SELLER)
        );
        assertTrue(exception.getMessage().contains("Insufficient privileges"));
    }

    @Test
    void validateRole_withNoAuthentication_shouldThrowUnauthorizedException() {
        // Act & Assert
        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> roleValidator.validateRole(UserRole.USER)
        );
        assertEquals("Authentication required", exception.getMessage());
    }

    @Test
    void getCurrentUserId_withValidAuthentication_shouldReturnUserId() {
        // Arrange
        Long expectedUserId = 123L;
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                expectedUserId, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        Long actualUserId = roleValidator.getCurrentUserId();

        // Assert
        assertEquals(expectedUserId, actualUserId);
    }
}
