package com.dotdot.marketplace.auth;

import com.dotdot.marketplace.auth.dto.AuthRequestDto;
import com.dotdot.marketplace.auth.dto.AuthResponseDto;
import com.dotdot.marketplace.auth.dto.RegisterRequest;
import com.dotdot.marketplace.auth.service.JwtProvider;
import com.dotdot.marketplace.user.entity.User;
import com.dotdot.marketplace.user.entity.UserRole;
import com.dotdot.marketplace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDto register(RegisterRequest request) {
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new RuntimeException("Login already exists");
        }
        var user = User.builder()
                .fullName(request.getFullName())
                .login(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthResponseDto.builder().token(jwtToken).build();
    }

    public AuthResponseDto authentication(AuthRequestDto request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
        var user = userRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new UsernameNotFoundException(request.getLogin()));
        var jwtToken = jwtService.generateToken(user);
        return AuthResponseDto.builder().token(jwtToken).build();
    }

    public AuthResponseDto refreshToken(String refreshToken) {
        String login;
        try {
            login = jwtService.extractUsername(refreshToken);
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token");
        }

        var user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String newAccessToken = jwtService.generateToken(user);

        return AuthResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
