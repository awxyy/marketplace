package com.dotdot.marketplace.configuration.jwt;


import com.dotdot.marketplace.user.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;


    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserPrincipal userPrincipal) {
        Map<String, Object> extraClaims = new HashMap<>();
        return generateToken(extraClaims, userPrincipal.getId(), userPrincipal.getUsername());
    }

    public String generateToken(Map<String, Object> extraClaims, Long userId, String username) {
        long now = System.currentTimeMillis();

        Map<String, Object> claims = new HashMap<>(extraClaims);
        claims.put("userId", userId);
        claims.put("role", "ROLE_USER");

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(now))
                .expiration(new Date(now + 1000 * 60 * 60 * 24))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpiration(token);
    }

    public Long extractUserId(String token) {
        return extractClaims(token, claims -> claims.get("userId", Long.class));
    }

    private boolean isTokenExpiration(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public Claims extractAllClaims(String token) {
        JwtParser parser = Jwts.parser().verifyWith(getSigningKey()).build();
        return parser.parseSignedClaims(token).getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
