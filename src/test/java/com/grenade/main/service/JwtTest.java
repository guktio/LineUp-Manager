package com.grenade.main.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@ActiveProfiles("test")
class JwtTest {

    private JwtProvider jwtProvider;

    private String secret;
    private long expiration= 3600;
    private  UUID uuid;
    @BeforeEach
    void setUp() {
        secret = "mySecretKeyForJwtThatIsLongEnough12345678901234567890";
        
        jwtProvider = new JwtProvider(secret, expiration);
        uuid = UUID.randomUUID();
    }

    @Test
    void shouldGenerateToken() {
        String token = jwtProvider.generateToken(uuid);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void shouldValidateToken() {
        String token = jwtProvider.generateToken(uuid);

        Boolean isValid = jwtProvider.validateToken(token);

        assertThat(isValid).isTrue();
    }

    @Test
    void shouldNotValidateExpiredToken() {
        jwtProvider.setExpiration(-1);
        String token = jwtProvider.generateToken(uuid);
        Boolean isValid = jwtProvider.validateToken(token);

        assertThat(isValid).isFalse();
    }

    @Test
    void shouldGetUsernameFromToken(){
        String token = jwtProvider.generateToken(uuid);
        UUID userUuid = UUID.fromString(jwtProvider.getUuidFromToken(token));

        assertThat(uuid).isEqualTo(userUuid);
    }

    @Test
    void shouldGenerateTwoSameTokens(){
        String token = jwtProvider.generateToken(uuid);
		String token2 = jwtProvider.generateToken(uuid);
		
		assertThat(token).isEqualTo(token2);
    }

    @Test
    void shouldGenerateTwoDifferentTokens(){
        String token = jwtProvider.generateToken(uuid);
		jwtProvider.validateToken(token);
        jwtProvider.setExpiration(678);
		String token2 = jwtProvider.generateToken(uuid);
		jwtProvider.validateToken(token2);
		
		assertThat(token).isNotEqualTo(token2);
    }

    @Test
    void shouldRejectTokenWithWrongKey() {
        String token = jwtProvider.generateToken(uuid);
        
        JwtProvider anotherProvider = new JwtProvider(
            "differentSecretKeyThatIsAlsoLongEnough1234567890123456",
            expiration
        );

        boolean isValid = anotherProvider.validateToken(token);

        assertThat(isValid).isFalse();
    }

    @Test
    void shouldRejectMalformedToken() {
        String badToken = "not.a.jwt";

        boolean isValid = jwtProvider.validateToken(badToken);

        assertThat(isValid).isFalse();
    }

    @Test
    void shouldRejectNullToken() {
        assertThat(jwtProvider.validateToken(null)).isFalse();
    }

    @Test
    void shouldRejectEmptyToken() {
        assertThat(jwtProvider.validateToken("")).isFalse();
    }

    @Test
    void shouldHaveCorrectExpirationTime() {
        String token = jwtProvider.generateToken(uuid);

        Claims claims = Jwts.parser()
                .verifyWith(jwtProvider.getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getExpiration())
                .isAfter(claims.getIssuedAt());
    }
}
