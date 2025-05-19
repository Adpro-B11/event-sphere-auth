package id.ac.ui.cs.advprog.eventsphereauth.security;

import id.ac.ui.cs.advprog.eventsphereauth.model.Role;
import id.ac.ui.cs.advprog.eventsphereauth.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private final String SECRET_KEY = "aVeryLongAndSecureSecretKeyForTestingHS256";
    private final long EXPIRATION_TIME_MS = TimeUnit.HOURS.toMillis(24);

    private UserDetails userDetails;
    private String testEmail = "test@example.com";
    private Key signingKey;

    @BeforeEach
    void setUp() {
        signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        ReflectionTestUtils.setField(jwtService, "secretKey", Base64.getEncoder().encodeToString(signingKey.getEncoded()));
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", EXPIRATION_TIME_MS);

        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(testEmail);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(signingKey)
                .setAllowedClockSkewSeconds(5)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Test
    void testGenerateTokenWithNoExtraClaims() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        Claims claims = extractAllClaims(token);
        assertEquals(testEmail, claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().getTime() > claims.getIssuedAt().getTime());
    }

    @Test
    void testGenerateTokenWithExtraClaims() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", Role.USER);
        extraClaims.put("customClaim", "value");

        String token = jwtService.generateToken(extraClaims, userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        Claims claims = extractAllClaims(token);
        assertEquals(testEmail, claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().getTime() > claims.getIssuedAt().getTime());

        assertEquals(Role.USER.toString(), claims.get("role"));
        assertEquals("value", claims.get("customClaim"));
    }

    @Test
    void testExtractUsernameFromToken() {
        String token = jwtService.generateToken(userDetails);
        String extractedUsername = jwtService.extractUsername(token);
        assertEquals(testEmail, extractedUsername);
    }

    @Test
    void testExtractClaimFromToken() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", Role.ADMIN.toString());
        String token = jwtService.generateToken(extraClaims, userDetails);

        String extractedRole = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
        assertEquals(Role.ADMIN.toString(), extractedRole);
    }

    @Test
    void testIsTokenValidTrue() {
        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.isTokenValid(token, userDetails);
        assertTrue(isValid);
    }

    @Test
    void testIsTokenValidFalseWrongUser() {
        String token = jwtService.generateToken(userDetails);

        UserDetails otherUserDetails = mock(UserDetails.class);
        when(otherUserDetails.getUsername()).thenReturn("other@example.com");

        boolean isValid = jwtService.isTokenValid(token, otherUserDetails);
        assertFalse(isValid);
    }

    @Test
    void testExtractExpiration() {
        String token = jwtService.generateToken(userDetails);
        Date expiration = jwtService.extractExpiration(token);
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void testIsTokenExpiredFalse() {
        String token = jwtService.generateToken(userDetails);
        boolean isExpired = jwtService.isTokenExpired(token);
        assertFalse(isExpired);
    }
}
