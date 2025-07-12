package com.dotdot.marketplace.jwt.controller;

import com.dotdot.marketplace.jwt.dto.AuthenticationRequest;
import com.dotdot.marketplace.jwt.dto.AuthenticationResponse;
import com.dotdot.marketplace.jwt.dto.RefreshTokenRequest;
import com.dotdot.marketplace.jwt.dto.RegisterRequest;
import com.dotdot.marketplace.jwt.service.AuthenticationService;
import com.dotdot.marketplace.jwt.service.JwtService;
import com.dotdot.marketplace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authentication(request));
    }

    @PostMapping("/login/access-token")
    public ResponseEntity<?> access(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();


        String login;
        try {
            login = jwtService.extractUsername(refreshToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        var user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String newAccessToken = jwtService.generateToken(user);

        return ResponseEntity.ok(
                AuthenticationResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken)
                        .build()
        );
    }
}
