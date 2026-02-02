package com.dotdot.marketplace.configuration.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtProvider jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwtToken = authHeader.substring(7);
        
        try {
            Long userId = jwtService.extractUserId(jwtToken);
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Extract roles from JWT token
                Claims claims = jwtService.extractAllClaims(jwtToken);
                Object rolesObj = claims.get("roles");
                List<GrantedAuthority> authorities;

                if (rolesObj instanceof List<?>) {
                    authorities = ((List<?>) rolesObj).stream()
                            .map(Object::toString)
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                } else if(rolesObj == null ) {
                    logger.warn("JWT token has null 'roles' claim for userId: {} from IP: {}. Request: {}",
                            userId, request.getRemoteAddr(), request.getRequestURI());
                    filterChain.doFilter(request, response);
                    return;
                } else {
                    logger.warn("JWT token has invalid 'roles' claim type. Expected List, GOT {} for userId: {} from IP: {}. Request: {}",
                            rolesObj.getClass().getSimpleName(), userId, request.getRemoteAddr(), request.getRequestURI());
                    filterChain.doFilter(request, response);
                    return;
                }
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            logger.debug("JWT token expired for request: {}", request.getRequestURI());
        } catch (io.jsonwebtoken.security.SecurityException e) {
            logger.warn("Invalid JWT signature for request: {} from IP: {}",
                    request.getRequestURI(), request.getRemoteAddr());
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            logger.warn("Malformed JWT token for request: {}", request.getRequestURI());
        } catch (Exception e) {
            logger.error("Unexpected error during JWT authentication for request: {}",
                    request.getRequestURI(), e);
        }
        // Invalid token, continue without authentication
        filterChain.doFilter(request, response);
    }
}
