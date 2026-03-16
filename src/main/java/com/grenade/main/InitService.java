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
        if(!userRepo.existsBySteamId("76561198848703847")){
            User user = User.builder()
                                .username("admin")
                                .password(passwordEncoder.encode("admin"))
                                .steamId("76561198848703847")
                                .uuid(UUID.randomUUID())
                                .role(User.RoleType.ADMIN)
                                .build();
            userRepo.save(user);
        }
    }
}
