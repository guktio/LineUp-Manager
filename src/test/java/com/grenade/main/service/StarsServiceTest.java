package com.grenade.main.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.grenade.main.entity.Grenade;
import com.grenade.main.entity.Stars;
import com.grenade.main.repo.GrenadeRepo;
import com.grenade.main.repo.StarsRepo;
import com.grenade.main.repo.UserRepo;

import com.grenade.main.entity.User;

@ExtendWith(MockitoExtension.class)
class StarsServiceTest {

    @InjectMocks StarsService service;
    @Mock UserRepo userRepo;
    @Mock GrenadeRepo grenadeRepo;
    @Mock StarsRepo starsRepo;

    UUID grenadeUuid;
    User user;
    Grenade grenade;

    @BeforeEach
    void setUp() {
        grenadeUuid = UUID.randomUUID();

        user = new User();
        user.setUuid(UUID.randomUUID());
        user.setUsername("testUser");

        grenade = new Grenade();
        grenade.setUuid(grenadeUuid);

        var auth = new UsernamePasswordAuthenticationToken("testUser", null, List.of());
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);

        when(userRepo.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(grenadeRepo.findByUuid(grenadeUuid)).thenReturn(Optional.of(grenade));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void toggleStar_starNotExists_addsStarAndReturnsTrue() {
        when(starsRepo.findByUserUuidAndGrenadeUuid(user.getUuid(), grenadeUuid))
                .thenReturn(Optional.empty());

        boolean result = service.toggleStar(grenadeUuid);

        assertTrue(result);
        verify(starsRepo).save(any(Stars.class));
        verify(grenadeRepo).increaseStars(grenadeUuid);
        verify(starsRepo, never()).delete(any());
        verify(grenadeRepo, never()).decreaseStars(any());
    }

    @Test
    void toggleStar_starExists_removesStarAndReturnsFalse() {
        Stars existingStar = new Stars();
        // стар уже есть — возвращаем его
        when(starsRepo.findByUserUuidAndGrenadeUuid(user.getUuid(), grenadeUuid))
                .thenReturn(Optional.of(existingStar));

        boolean result = service.toggleStar(grenadeUuid);

        assertFalse(result);
        verify(starsRepo).delete(existingStar);
        verify(grenadeRepo).decreaseStars(grenadeUuid);
        verify(starsRepo, never()).save(any());
        verify(grenadeRepo, never()).increaseStars(any());
    }
}