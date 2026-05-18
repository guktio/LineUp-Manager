package com.grenade.main.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.grenade.main.entity.User;
import com.grenade.main.repo.UserRepo;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {
    
    @Mock UserRepo userRepo;

    @InjectMocks UserDetailsServiceImpl service;

    final String username = "testUser";
    final UUID uuid = UUID.randomUUID();
    final String email = "testUser@mail.com";
    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setUuid(uuid);
    }

    @Test
    void loadUserByUsername_found_returnsUser() {
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        User result = service.loadUserByUsername(email);

        assertEquals(username, result.getUsername());
    }

    @Test
    void loadUserByUsername_notFound_throwsUsernameNotFoundException() {
        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername(email));
    }

    @Test
    void loadUserByUuid_found_returnsUser() {
        when(userRepo.findByUuid(uuid)).thenReturn(Optional.of(user));

        User result = service.loadUserByUuid(uuid);

        assertEquals(uuid, result.getUuid());
    }

    @Test
    void loadUserByUuid_notFound_throwsEntityNotFoundException() {
        when(userRepo.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.loadUserByUuid(uuid));
    }

}
