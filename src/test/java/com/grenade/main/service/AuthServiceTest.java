package com.grenade.main.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.grenade.main.dto.AuthRequest;
import com.grenade.main.dto.AuthResponse;
import com.grenade.main.entity.User;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUserSuccessfully() {

        AuthRequest request = new AuthRequest("ivan", "test@mail.com", "1234");

        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setUsername("ivan");

        when(passwordEncoder.encode("1234")).thenReturn("encoded1234");
        when(userService.create(any())).thenReturn(user);
        when(jwtProvider.generateToken(user.getUuid())).thenReturn("token123");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("token123", response.getToken());

        verify(passwordEncoder).encode("1234");
        verify(userService).create(any());
        verify(jwtProvider).generateToken(user.getUuid());
    }

    @Test
    void shouldLoginUserSuccessfully() {
        AuthRequest request = new AuthRequest("ivan", "ivan@gmail.com","1234");

        User user = new User();
        user.setUuid(UUID.randomUUID());

        Authentication auth = new UsernamePasswordAuthenticationToken(
            user,
            null,
            List.of()
        );

        when(authenticationManager.authenticate(any()))
                .thenReturn(auth);

        when(jwtProvider.getUuidFromToken(anyString())).thenReturn(UUID.randomUUID().toString());
        when(jwtProvider.generateToken(user.getUuid())).thenReturn("jwt");

        AuthResponse response = authService.login(request);

        assertEquals("jwt", response.getToken());

        verify(authenticationManager).authenticate(any());
    }

    @Test
    void shouldLoginUserUnsuccessfully() {
        AuthRequest request = new AuthRequest("ivan", "ivan@gmail.com","1234");

        User user = new User();
        user.setUuid(UUID.randomUUID());

        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(auth);

        when(auth.getPrincipal()).thenReturn(user);
        when(jwtProvider.getUuidFromToken(anyString())).thenReturn(UUID.randomUUID().toString());
        when(jwtProvider.generateToken(user.getUuid())).thenReturn("jwt");

        AuthResponse response = authService.login(request);

        assertEquals("jwt", response.getToken());

        verify(authenticationManager).authenticate(any());
    }
}
