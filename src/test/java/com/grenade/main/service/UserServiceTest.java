package com.grenade.main.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.grenade.main.dto.UserDTO;
import com.grenade.main.dto.UserRequest;
import com.grenade.main.repo.UserRepo;

import jakarta.persistence.EntityNotFoundException;

import com.grenade.main.entity.SteamProfile;
import com.grenade.main.entity.User;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock UserRepo userRepo;

    @Mock SteamService steamService;

    @Mock BCryptPasswordEncoder passwordEncoder;

    @InjectMocks UserService userService;

    @Test
    void shouldReturnTrue() {
        String steamId = "321";

        when(userRepo.existsBySteamId(steamId)).thenReturn(true);

        boolean result = userService.isUserExistBySteamId(steamId);

        assertTrue(result);
        verify(userRepo).existsBySteamId(steamId);
    }

    @Test
    void shouldThrowException() {
        String steamId = "321";

        when(userRepo.existsBySteamId(steamId)).thenReturn(false);

        boolean result = userService.isUserExistBySteamId(steamId);

        assertFalse(result);
        verify(userRepo).existsBySteamId(steamId);
    }

    @Test
    void shouldReturnUserBySteamId(){
        SteamProfile steam = SteamProfile
                            .builder()
                            .steamId("123")
                            .build(); 
        User user = new User();
        user.setSteamProfile(steam);

        when(userRepo.findBySteamId(steam.getSteamId()))
                .thenReturn(Optional.of(user));

        User result = userService.getBySteamId(steam.getSteamId());

        assertEquals(user, result);
        verify(userRepo).findBySteamId(steam.getSteamId());
    }

    @Test
    void shouldFindByUuidWhenExists(){
        UUID uuid = UUID.randomUUID();

        User user = new User();
        user.setUuid(uuid);

        when(userRepo.findByUuid(uuid)).thenReturn(Optional.of(user));

        UserDTO result = userService.findByUuid(uuid);

        assertEquals(userService.toDTO(user), result);
        verify(userRepo).findByUuid(uuid);
    }

    @Test
    void shouldThrowExceptionWhenUserNotExists(){
        UUID uuid = UUID.randomUUID();

        when(userRepo.findByUuid(uuid)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class, 
                () -> userService.findByUuid(uuid));

        assertTrue(ex.getMessage().contains(uuid.toString()));
        verify(userRepo).findByUuid(uuid);
    }

    @Test
    void shouldCreateUser_withPassword() {
        UserRequest req = UserRequest.builder()
                        .email("test@mail.com")
                        .username("ivan")
                        .password("12345")
                        .build();

        when(userRepo.existsByEmail(req.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("12345")).thenReturn("encoded");
        when(userRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.create(req);

        assertEquals("encoded", result.getPassword());

        verify(passwordEncoder).encode("12345");
    }

    @Test
    void shouldThrowException_whenEmailExists() {
        UserRequest req = UserRequest.builder().email("test@mail.com").build();

        when(userRepo.existsByEmail(req.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> userService.create(req));

        verify(userRepo).existsByEmail(req.getEmail());
        verify(userRepo, never()).save(any());
    }

    @Test
    void shouldThrowException_whenSteamIdExists() {
        UserRequest req = UserRequest.builder().email("test@mail.com").steamId("123").build();

        when(userRepo.existsBySteamId(req.getSteamId())).thenReturn(true);

        assertThrows(RuntimeException.class,
                () -> userService.create(req));

        verify(userRepo).existsBySteamId(req.getSteamId());
        verify(userRepo, never()).save(any());
    }

    

}
