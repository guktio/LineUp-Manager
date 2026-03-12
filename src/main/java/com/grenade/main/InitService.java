package com.grenade.main;

import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.grenade.main.entity.User;
import com.grenade.main.repo.UserRepo;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class InitService {

    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostConstruct
    @SuppressWarnings("null")
    public void init() {
            User user = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
                    .steamId("76561198848703847")
                    .uuid(UUID.randomUUID())
                    .role(User.RoleType.ADMIN)
                    .build();
            userRepo.save(user);
            // List<String> buttons = List.of("W","LMB");
            // for(long i = 0; i < 20;i++) {
            //     Grenade g = Grenade.builder()
            //             .map(MapType.DE_DUST2)
            //             .name("Full cover mid doors" + i)
            //             .description("Full cover mid doors")
            //             .approved(true)
            //             .ready(true)
            //             .author(user)
            //             .side(Side.CT)
            //             .grenadeType(GrenadeType.weapon_smokegrenade)
            //             .thumbnail("5c644fcd-9b9c-4ac5-aeb2-f4d9578a3bd3.jpg")
            //             .media("fd3ed35b-cb61-41ec-b9fc-7e6662c66212.mp4")
            //             .command("setpos_exact 375.937195 -463.992188 1.083763;setang -17.397451 111.853889 0.000000")
            //             .buttons(buttons)
            //             .stars(i)
            //             .build();
            //     grenadeRepo.save(g);
            }
        // for(long i = 21; i < 40;i++) {
        //         GrenadeRequest g = GrenadeRequest.builder()
        //                 .map(MapType.DUST2)
        //                 .description("Full cover mid doors")
        //                 .grenadeType(GrenadeType.SMOKE)
        //                 .command("setpos_exact 375.937195 -463.992188 1.083763;setang -17.397451 111.853889 0.000000")
        //                 .buttons(buttons)
        //                 .build();
        //         grenadeService.create(g);
        //     }
    }
