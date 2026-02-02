package com.dotdot.marketplace.configuration.security;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RoleSecurityAspect {

    private final RoleValidator roleValidator;

    @Before("@annotation(requireRole)")
    public void checkRole(RequireRole requireRole) {
        roleValidator.validateRole(requireRole.message(), requireRole.value());
    }
}
