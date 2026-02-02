package com.dotdot.marketplace.configuration.security;

import com.dotdot.marketplace.exception.UnauthorizedException;
import com.dotdot.marketplace.user.entity.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class RoleValidator {

    public void validateRole(UserRole... requiredRoles) {
        validateRole("Insufficient privileges. Required roles: " + 
            Arrays.toString(requiredRoles), requiredRoles);
    }

    public void validateRole(String message, UserRole... requiredRoles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Authentication required");
        }

        List<String> userRoles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        boolean hasRequiredRole = Arrays.stream(requiredRoles)
                .map(role -> "ROLE_" + role.name())
                .anyMatch(userRoles::contains);

        if (!hasRequiredRole) {
            throw new UnauthorizedException(message);
        }
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Authentication required");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        
        throw new UnauthorizedException("Invalid authentication principal");
    }
}
