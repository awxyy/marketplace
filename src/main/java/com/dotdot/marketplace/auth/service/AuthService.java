package com.dotdot.marketplace.auth.service;

import com.dotdot.marketplace.auth.dto.AuthRequestDto;
import com.dotdot.marketplace.auth.dto.AuthResponseDto;
import com.dotdot.marketplace.auth.dto.RegisterRequest;
import com.dotdot.marketplace.configuration.jwt.JwtProvider;
import com.dotdot.marketplace.exception.InvalidRefreshTokenException;
import com.dotdot.marketplace.user.entity.Role;
import com.dotdot.marketplace.user.entity.User;
import com.dotdot.marketplace.user.entity.UserRole;
import com.dotdot.marketplace.user.repository.RoleRepository;
import com.dotdot.marketplace.user.repository.UserRepository;
import com.dotdot.marketplace.user.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDto register(RegisterRequest request) {
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new RuntimeException("Login already exists");
        }

        Set<UserRole> userRoles = request.getRoles();
        if (userRoles == null || userRoles.isEmpty()) {
            userRoles = Set.of(UserRole.USER);
        }

        Set<Role> roles = userRoles.stream()
                .map(userRole -> roleRepository.findByName(userRole)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + userRole)))
                .collect(Collectors.toSet());

        var user = User.builder()
                .fullName(request.getFullName())
                .login(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);

        var jwtToken = jwtProvider.generateToken(new UserPrincipal(user));
        return AuthResponseDto.builder().accessToken(jwtToken).build();
    }

    public AuthResponseDto login(AuthRequestDto request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
        var user = userRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new UsernameNotFoundException(request.getLogin()));
        var jwtToken = jwtProvider.generateToken(new UserPrincipal(user));
        return AuthResponseDto.builder().accessToken(jwtToken).build();
    }

    public AuthResponseDto refreshToken(String refreshToken) {
        String login;
        try {
            login = jwtProvider.extractUsername(refreshToken);
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token");
        }

        var user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!jwtProvider.validateToken(refreshToken, new UserPrincipal(user))) {
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        String newAccessToken = jwtProvider.generateToken(new UserPrincipal(user));

        return AuthResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
