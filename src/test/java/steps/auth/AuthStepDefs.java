package steps.auth;

import com.dotdot.marketplace.auth.dto.AuthRequestDto;
import com.dotdot.marketplace.auth.dto.AuthResponseDto;
import com.dotdot.marketplace.auth.dto.RegisterRequest;
import com.dotdot.marketplace.configuration.jwt.JwtProperties;
import com.dotdot.marketplace.user.entity.UserRole;
import io.cucumber.java.en.Given;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.GrantedAuthority;
import steps.TestContext;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthStepDefs {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TestContext context;

    @Given("a registered user exists with login {string} and password {string} with role {string}")
    public void aRegisteredUserExists(String login, String password, String role) {
        context.setCurrentLogin(login);

        RegisterRequest registerRequest = RegisterRequest.builder()
                .login(login)
                .password(password)
                .fullName("Test User")
                .roles(Collections.singleton(UserRole.valueOf(role)))
                .build();

        Response regResponse = given()
                .contentType(ContentType.JSON)
                .body(registerRequest)
                .post("/auth/register");

        assertTrue(regResponse.getStatusCode() == 200 || regResponse.getStatusCode() == 400,
                "User registration failed");
    }

    @Given("the user logs in with login {string} and password {string}")
    public void theUserLogsIn(String login, String password) {
        context.setCurrentLogin(login);
        AuthRequestDto authRequest = AuthRequestDto.builder().login(login).password(password).build();

        Response loginResponse = given()
                .contentType(ContentType.JSON)
                .body(authRequest)
                .post("/auth/login");

        assertEquals(200, loginResponse.getStatusCode(), "Login failed!");

        AuthResponseDto authResponse = loginResponse.as(AuthResponseDto.class);
        context.setAccessToken(authResponse.getAccessToken());
        context.setRequest(given().header("Authorization", "Bearer " + context.getAccessToken()));
    }

    @Given("the authorization state is altered to {string}")
    public void theAuthorizationStateIsAlteredTo(String state) {
        switch (state) {
            case "MISSING":
                context.setRequest(given());
                break;
            case "INVALID":
                context.setRequest(given().header("Authorization", "Bearer invalid-signature-token-xyz"));
                break;
            case "EXPIRED":
                var userDetails = userDetailsService.loadUserByUsername("seller_john");
                String expiredToken = generateExpiredTokenDirectly(userDetails);
                context.setRequest(given().header("Authorization", "Bearer " + expiredToken));
                break;
            case "INSUFFICIENT_ROLE":
                aRegisteredUserExists("regular_user", "Pass123!", "USER");
                theUserLogsIn("regular_user", "Pass123!");
                break;
        }
    }

    private String generateExpiredTokenDirectly(org.springframework.security.core.userdetails.UserDetails userDetails) {
        long now = System.currentTimeMillis();
        long tenMinutesAgo = now - (1000 * 60 * 10);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userDetails.getUsername());
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());

        claims.put("iat", new Date(tenMinutesAgo - (1000 * 60)));
        claims.put("exp", new Date(tenMinutesAgo));

        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        SecretKey signingKey = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .claims(claims)
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }
}