package com.grenade.main.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@ActiveProfiles("test")
class JwtTest {

    private JwtProvider jwtProvider;

    private String testUsername;
    private String secret;
    private long expiration= 3600;

    @BeforeEach
    void setUp() {
        secret = "mySecretKeyForJwtThatIsLongEnough12345678901234567890";
        
        jwtProvider = new JwtProvider(secret, expiration);
        testUsername = "testname";
    }

    @Test
    void shouldGenerateToken() {
        String token = jwtProvider.generateToken(testUsername);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void shouldValidateToken() {
        String token = jwtProvider.generateToken(testUsername);

        Boolean isValid = jwtProvider.validateToken(token);

        assertThat(isValid).isTrue();
    }

    @Test
    void shouldNotValidateExpiredToken() {
        jwtProvider.setExpiration(-1);
        String token = jwtProvider.generateToken(testUsername);
        Boolean isValid = jwtProvider.validateToken(token);

        assertThat(isValid).isFalse();
    }

    @Test
    void shouldGetUsernameFromToken(){
        String token = jwtProvider.generateToken(testUsername);
        String username = jwtProvider.getUsernameFromToken(token);

        assertThat(username).isEqualTo("testname");
    }

    @Test
    void shouldGenerateTwoSameTokens(){
        String token = jwtProvider.generateToken(testUsername);
		String token2 = jwtProvider.generateToken(testUsername);
		
		assertThat(token).isEqualTo(token2);
    }

    @Test
    void shouldGenerateTwoDifferentTokens(){
        String token = jwtProvider.generateToken(testUsername);
		jwtProvider.validateToken(token);
        jwtProvider.setExpiration(678);
		String token2 = jwtProvider.generateToken(testUsername);
		jwtProvider.validateToken(token2);
		
		assertThat(token).isNotEqualTo(token2);
    }

    @Test
    void shouldRejectTokenWithWrongKey() {
        String token = jwtProvider.generateToken(testUsername);
        
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
    void shouldRejectTamperedToken() {
        String token = jwtProvider.generateToken(testUsername);

        String tampered = token.substring(0, token.length() - 1) + "x";

        assertThat(jwtProvider.validateToken(tampered)).isFalse();
    }

    @Test
    void shouldThrowExceptionForInvalidTokenWhenGettingUsername() {
        assertThatThrownBy(() -> jwtProvider.getUsernameFromToken("invalid"))
                .isInstanceOf(Exception.class);
    }

    @Test
    void shouldHaveCorrectExpirationTime() {
        String token = jwtProvider.generateToken(testUsername);

        Claims claims = Jwts.parser()
                .verifyWith(jwtProvider.getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getExpiration())
                .isAfter(claims.getIssuedAt());
    }
    
    @Test
    void shouldReturnCorrectUsername() {
        String token = jwtProvider.generateToken("alice");

        String username = jwtProvider.getUsernameFromToken(token);

        assertThat(username).isEqualTo("alice");
    }
}
