package com.dotdot.marketplace.auth;

import com.dotdot.marketplace.auth.dto.AuthRequestDto;
import com.dotdot.marketplace.auth.dto.AuthResponseDto;
import com.dotdot.marketplace.auth.dto.RefreshTokenDto;
import com.dotdot.marketplace.auth.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto request) {
        return ResponseEntity.ok(authService.authentication(request));
    }

    @PostMapping("/login/access-token")
    public ResponseEntity<?> access(@RequestBody RefreshTokenDto request) {
        try {
            AuthResponseDto response = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
