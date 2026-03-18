package com.grenade.main.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grenade.main.dto.GrenadeRequest;
import com.grenade.main.dto.GrenadeResponse;
import com.grenade.main.entity.Grenade;
import com.grenade.main.entity.Grenade.GrenadeType;
import com.grenade.main.entity.Grenade.MapType;
import com.grenade.main.entity.User;
import com.grenade.main.repo.GrenadeRepo;
import com.grenade.main.repo.UserRepo;



@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class GrenadeServiceTest {

    @Autowired
    private GrenadeRepo grenadeRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private GrenadeService grenadeService;

    @SuppressWarnings("null")
    @Test
    @WithMockUser(username = "john", roles = {"USER"})
    void shouldCreateGrenade(){
        User user = User.builder()
            .username("john")
            .password("encoded")
            .steamId("steam123")
            .build();

        userRepo.save(user);

        GrenadeRequest grenadeRequest = GrenadeRequest.builder()
                                        .map(MapType.DE_DUST2)
                                        .grenadeType(GrenadeType.weapon_smokegrenade)
                                        .side("CT")
                                        .speed("270")
                                        .command("testCommand")
                                        .buttons(List.of("W","Jump"))
                                        .description("testDescription")
                                        .build();
        GrenadeResponse result = grenadeService.create(grenadeRequest);

        assertThat(result).isNotNull();
        assertThat(result.uuid()).isNotNull();
        assertThat(result.name()).isNotNull();
        assertThat(result.map()).isEqualTo(MapType.DE_DUST2);

        Grenade grdb = grenadeRepo.findByUuid(result.uuid()).orElseThrow();
        assertThat(grdb.getName()).isEqualTo(result.name());
    }

    @SuppressWarnings("null")
    @Test
    @WithMockUser(username = "john", roles = {"USER"})
    void shouldKnewIfGrenadeStarredByUser(){
        User user = User.builder()
            .username("john")
            .password("encoded")
            .steamId("steam123")
            .build();

        userRepo.save(user);

        GrenadeRequest grenadeRequest = GrenadeRequest.builder()
                                        .map(MapType.DE_DUST2)
                                        .grenadeType(GrenadeType.weapon_smokegrenade)
                                        .side("CT")
                                        .speed("270")
                                        .command("testCommand")
                                        .buttons(List.of("W","Jump"))
                                        .description("testDescription")
                                        .build();
        GrenadeResponse result = grenadeService.create(grenadeRequest);
        Boolean isStared = grenadeService.isStaredByUser(result.uuid());

        assertThat(isStared).isFalse();
    }


}
