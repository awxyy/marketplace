package com.dotdot.marketplace.jwt.service;

import com.dotdot.marketplace.jwt.dto.AuthenticationRequest;
import com.dotdot.marketplace.jwt.dto.AuthenticationResponse;
import com.dotdot.marketplace.jwt.dto.RegisterRequest;
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
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
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
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse authentication(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
        var user = userRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new UsernameNotFoundException(request.getLogin()));
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }
}
