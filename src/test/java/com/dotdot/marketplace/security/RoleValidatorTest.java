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
        SecurityContextHolder.clearContext();
    }

    @Test
    void validateRole_withValidSellerRole_shouldPass() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                1L, null, List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_SELLER")
        )
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertDoesNotThrow(() -> roleValidator.validateRole(UserRole.SELLER));
    }

    @Test
    void validateRole_withValidUserRole_shouldPass() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                1L, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertDoesNotThrow(() -> roleValidator.validateRole(UserRole.USER));
    }

    @Test
    void validateRole_withInvalidRole_shouldThrowUnauthorizedException() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                1L, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> roleValidator.validateRole(UserRole.SELLER)
        );
        assertTrue(exception.getMessage().contains("Insufficient privileges"));
    }

    @Test
    void validateRole_withOneOfMultipleRequiredRoles_shouldPass() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                1L, null, List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_SELLER")
        )
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertDoesNotThrow(() -> roleValidator.validateRole(UserRole.USER, UserRole.SELLER));
    }

    @Test
    void validateRole_withNoneOfMultipleRequiredRoles_shouldThrowUnauthorizedException() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                1L, null, List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> roleValidator.validateRole(UserRole.USER)
        );
        assertTrue(exception.getMessage().contains("Insufficient privileges"));
    }

    @Test
    void validateRole_withAllRequiredRoles_shouldPass() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                1L, null, List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_SELLER")
        )
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertDoesNotThrow(() -> roleValidator.validateRole(UserRole.USER, UserRole.SELLER));
    }

    @Test
    void validateRole_withEmptyRoles_shouldThrowUnauthorizedException() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                1L, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> roleValidator.validateRole()
        );
        assertTrue(exception.getMessage().contains("Insufficient privileges"));
    }

    @Test
    void validateRole_withNoAuthentication_shouldThrowUnauthorizedException() {
        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> roleValidator.validateRole(UserRole.USER)
        );
        assertEquals("Authentication required", exception.getMessage());
    }

    @Test
    void getCurrentUserId_withValidAuthentication_shouldReturnUserId() {
        Long expectedUserId = 123L;
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                expectedUserId, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        Long actualUserId = roleValidator.getCurrentUserId();

        assertEquals(expectedUserId, actualUserId);
    }

    @Test
    void validateRole_sellerCanAccessUserEndpoints() {
        // SELLER може заходити на USER ендпоінти
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                1L, null, List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_SELLER")
        )
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertDoesNotThrow(() -> roleValidator.validateRole(UserRole.USER));
    }

    @Test
    void validateRole_userCannotAccessSellerEndpoints() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                1L, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> roleValidator.validateRole(UserRole.SELLER)
        );
        assertTrue(exception.getMessage().contains("Insufficient privileges"));
    }
}
