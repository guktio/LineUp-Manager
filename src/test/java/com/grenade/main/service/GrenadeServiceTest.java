package com.grenade.main.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import com.grenade.main.dto.GrenadeRequest;
import com.grenade.main.dto.GrenadeResponse;
import com.grenade.main.dto.PageDTO;
import com.grenade.main.entity.Grenade;
import com.grenade.main.entity.Media;
import com.grenade.main.entity.User;
import com.grenade.main.entity.Grenade.GrenadeType;
import com.grenade.main.entity.Grenade.MapType;
import com.grenade.main.entity.User.RoleType;
import com.grenade.main.repo.GrenadeRepo;
import com.grenade.main.repo.MediaRepo;
import com.grenade.main.repo.StarsRepo;
import com.grenade.main.repo.UserRepo;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class GrenadeServiceTest {

    @Mock GrenadeRepo grenadeRepo;

    @Mock StarsRepo starsRepo;

    @Mock MediaRepo mediaRepo;

    @Mock MediaService mediaService;

    @Mock UserRepo userRepo;

    @InjectMocks GrenadeService grenadeService;
    
    @Test
    void testGenerateNameWithSeed() {
        Random fixedRandom = new Random(42);

        String result = grenadeService.generateName(fixedRandom);
        
        assertEquals("blood_blade", result); 
    }

    @Test
    void shouldCreateGrenadeNoMedia(){

        GrenadeRequest grnd = GrenadeRequest.builder().build();

        User user = User.builder().username("john").email("john@mail.com").build();

        when(grenadeRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        GrenadeResponse result = grenadeService.create(grnd, user);
        assertThat(result).isNotNull();

        verify(grenadeRepo).save(any(Grenade.class));
    }

    @Test
    void shouldCreateGrenadeWithMedia(){

        Media media = Media.builder().build();

        when(mediaRepo.findByUuid(media.getUuid())).thenReturn(Optional.of(media));
        GrenadeRequest grnd = GrenadeRequest.builder().media(media.getUuid()).build();

        User user = User.builder().username("john").email("john@mail.com").build();

        when(grenadeRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        GrenadeResponse result = grenadeService.create(grnd, user);
        assertThat(result).isNotNull();

        
        verify(mediaRepo).findByUuid(media.getUuid());
        verify(grenadeRepo).save(any(Grenade.class));
    }

    @Test
    void shouldThrowMediaException_create(){

        Media media = Media.builder().build();

        when(mediaRepo.findByUuid(media.getUuid())).thenReturn(Optional.empty());
        GrenadeRequest grnd = GrenadeRequest.builder().media(media.getUuid()).build();
        
        User user = User.builder().username("john").email("john@mail.com").build();
        
        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class, 
                () -> grenadeService.create(grnd, user));
        
        assertTrue(ex.getMessage().contains(media.getUuid().toString()));
        verify(mediaRepo).findByUuid(media.getUuid());
    }

    @Test
    void shouldReturnGrenadeByUuid(){
        User author = User.builder().email("john@mail.com").build();

        Grenade grnd = Grenade.builder().author(author).build();

        when(grenadeRepo.findByUuid(grnd.getUuid())).thenReturn(Optional.of(grnd));

        GrenadeResponse result = grenadeService.getByUuid(grnd.getUuid());

        verify(grenadeRepo).findByUuid(grnd.getUuid());
        assertEquals(grnd.getUuid(), result.uuid());

    }

    @Test
    void shouldThrowNotFoundException_getByUuid(){
        Grenade grnd = Grenade.builder().build();

        when(grenadeRepo.findByUuid(grnd.getUuid())).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class, 
                () -> grenadeService.getByUuid(grnd.getUuid()));

        assertTrue(ex.getMessage().contains(grnd.getUuid().toString()));
        verify(grenadeRepo).findByUuid(grnd.getUuid());
    }

    @Test
    void shouldUpdateGrenade() {
        Media media = Media.builder().build();

        User owner = User.builder()
                .email("john@mail.com")
                .build();

        Grenade existing = Grenade.builder()
                .name("Old")
                .description("Old desc")
                .author(owner)
                .build();

        GrenadeRequest request = GrenadeRequest.builder()
                .command("new command")
                .map(MapType.DE_ANCIENT)
                .grenadeType(GrenadeType.weapon_flashbang)
                .side("T")
                .speed("270")
                .buttons(List.of("LMB","W"))
                .media(media.getUuid())
                .build();

        when(grenadeRepo.findByUuid(existing.getUuid()))
                .thenReturn(Optional.of(existing));

        when(grenadeRepo.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

          when(mediaRepo.findByUuid(media.getUuid()))
            .thenReturn(Optional.of(media));

        GrenadeResponse result = grenadeService.update(existing.getUuid(), request, owner);

        assertEquals("new command", result.command());
        assertEquals(MapType.DE_ANCIENT, result.map());
        assertEquals(GrenadeType.weapon_flashbang, result.grenadeType());
        assertEquals("T", result.side());
        assertEquals("270", result.speed());
        assertEquals(List.of("LMB","W"), result.buttons());



        verify(grenadeRepo).save(any(Grenade.class));
    }

    @Test
    void shouldNotUpdateGrenade_UserIsNotOwner() {

        User owner = User.builder()
                .email("john@mail.com")
                .build();

        User notOwner = User.builder()
                .email("john2@mail.com")
                .build();

        Grenade existing = Grenade.builder()
                .name("Old")
                .description("Old desc")
                .author(owner)
                .build();

        when(grenadeRepo.findByUuid(existing.getUuid()))
                .thenReturn(Optional.of(existing));

        assertThrows(
                AccessDeniedException.class,
                () -> grenadeService.update(
                        existing.getUuid(),
                        GrenadeRequest.builder().build(),
                        notOwner
                )
        );
    }

    @Test
    void shouldUpdateGrenade_UserIsAdmin() {

        User owner = User.builder()
                .email("john@mail.com")
                .build();

        User notOwner = User.builder()
                .email("john2@mail.com")
                .role(RoleType.ADMIN)
                .build();

        Grenade existing = Grenade.builder()
                .name("Old")
                .description("Old desc")
                .author(owner)
                .build();
            
         GrenadeRequest request = GrenadeRequest.builder()
                .name("New")
                .description("New desc")
                .build();

        when(grenadeRepo.findByUuid(existing.getUuid()))
                .thenReturn(Optional.of(existing));

        when(grenadeRepo.findByUuid(existing.getUuid()))
                .thenReturn(Optional.of(existing));

        when(grenadeRepo.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        GrenadeResponse result = grenadeService.update(existing.getUuid(), request, notOwner);

        assertEquals("New", result.name());
        assertEquals("New desc", result.description());

        verify(grenadeRepo).save(any(Grenade.class));
    }

    @Test
    void shouldReturnGrenadeByFilter(){
        User user = User.builder()
                .username("john")
                .email("john@mail.com")
                .build();

        Grenade grnd = Grenade.builder()
                .name("Old")
                .description("Old desc")
                .grenadeType(GrenadeType.weapon_flashbang)
                .author(user)
                .build();
                
        Pageable pageable = PageRequest.of(0, 5);
        Page<Grenade> page = new PageImpl<>(List.of(grnd));

        when(grenadeRepo.findByFilter(pageable, 
                null, GrenadeType.weapon_flashbang,null,null,null))
                        .thenReturn(page);

        PageDTO<GrenadeResponse> result = grenadeService.getByFilter(pageable, 
                null, GrenadeType.weapon_flashbang, null, null, null);

        assertThat(result.items().get(0).name()).isEqualTo(grnd.getName());
        assertThat(result.items().get(0).uuid()).isEqualTo(grnd.getUuid());
    }

     @Test
    void shouldReturnGrenadeByFilter_withAuthor_likedByUser(){
        User user = User.builder()
                .email("john@mail.com")
                .build();

        Grenade grnd = Grenade.builder()
                .name("Old")
                .description("Old desc")
                .author(user)
                .build();
                
        Pageable pageable = PageRequest.of(0, 5);
        Page<Grenade> page = new PageImpl<>(List.of(grnd));

        when(grenadeRepo.findByFilter(pageable, 
                null, null,user.getId(),null,user.getId()))
                        .thenReturn(page);
        when(userRepo.findByUuid(user.getUuid()))
                .thenReturn(Optional.of(user));

        PageDTO<GrenadeResponse> result = grenadeService.getByFilter(pageable, 
                null, null, user.getUuid(), null, user.getUuid());

        assertThat(result.items().get(0).name()).isEqualTo(grnd.getName());
        assertThat(result.items().get(0).uuid()).isEqualTo(grnd.getUuid());
    }

    @Test
    void shouldReturnUnreadyGreande(){
        Pageable pageable = PageRequest.of(0, 5);

        User user = User.builder()
                .email("john@mail.com")
                .build();

        Grenade grnd = Grenade.builder()
                .name("Old")
                .description("Old desc")
                .author(user)
                .ready(false)
                .build();

        Page<Grenade> page = new PageImpl<>(List.of(grnd));
        

        when(userRepo.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(grenadeRepo.findUnreadyByAuthor(pageable, user.getId()))
                .thenReturn(page);
        
        PageDTO<GrenadeResponse> result = grenadeService.getUreadyGrenade(pageable, user);
        
        assertThat(result.items().get(0).uuid()).isEqualTo(grnd.getUuid());
    }

    @Test
    void shouldThrowEntityNotFound_getUreadyGrenade(){
        Pageable pageable = PageRequest.of(1, 5);

        User user = User.builder()
                .email("john@mail.com")
                .build();

        // Grenade grnd = Grenade.builder()
        //         .name("Old")
        //         .description("Old desc")
        //         .author(user)
        //         .ready(false)
        //         .build();

        when(userRepo.findByEmail(user.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> grenadeService.getUreadyGrenade(
                        pageable,
                        user
                )
        );
    }

}
