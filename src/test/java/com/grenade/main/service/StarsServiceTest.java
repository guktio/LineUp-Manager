package com.grenade.main.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.grenade.main.entity.Grenade;
import com.grenade.main.repo.GrenadeRepo;
import com.grenade.main.repo.StarsRepo;
import com.grenade.main.repo.UserRepo;
import com.grenade.main.entity.User;

@ExtendWith(MockitoExtension.class)
public class StarsServiceTest {
    
    @InjectMocks
    private StarsService service;

    @Mock
    private UserRepo userRepo;

    @Mock
    private GrenadeRepo grenadeRepo;

    @Mock
    private StarsRepo starsRepo;

    @Test
    void shouldAddStar_whenNotExists() {
        String username = "testUser";

        var auth = new UsernamePasswordAuthenticationToken(
                username, null, List.of()
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        UUID grenadeUuid = UUID.randomUUID();

        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setUsername(username);

        Grenade grenade = new Grenade();
        grenade.setUuid(grenadeUuid);

        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(grenadeRepo.findByUuid(grenadeUuid)).thenReturn(Optional.of(grenade));
        when(starsRepo.findByUserUuidAndGrenadeUuid(user.getUuid(), grenadeUuid))
                .thenReturn(Optional.empty());

        // when
        boolean result = service.toggleStar(grenadeUuid);

        // then
        assertTrue(result);
        verify(starsRepo).save(any());
        verify(grenadeRepo).increaseStars(grenadeUuid);

        SecurityContextHolder.clearContext();
    }

     @Test
    void shouldRemoveStar_whenExists() {
        String username = "testUser";

        var auth = new UsernamePasswordAuthenticationToken(
                username, null, List.of()
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        UUID grenadeUuid = UUID.randomUUID();

        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setUsername(username);

        Grenade grenade = new Grenade();
        grenade.setUuid(grenadeUuid);

        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(grenadeRepo.findByUuid(grenadeUuid)).thenReturn(Optional.of(grenade));
        when(starsRepo.findByUserUuidAndGrenadeUuid(user.getUuid(), grenadeUuid))
                .thenReturn(Optional.empty());

        // when
        service.toggleStar(grenadeUuid);
        boolean result = service.toggleStar(grenadeUuid);
        // then
        assertTrue(result);
        verify(starsRepo).save(any());
        verify(grenadeRepo).increaseStars(grenadeUuid);

        SecurityContextHolder.clearContext();
    }
}
