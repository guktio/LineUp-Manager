package com.grenade.main;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        private final static Logger logger = LoggerFactory.getLogger(InitService.class);


    @PostConstruct
    @SuppressWarnings("null")
    public void init() {
        if(!userRepo.existsBySteamId("76561198848703847")){
            User user = User.builder()
                                .username("admin")
                                .email("admin")
                                .password(passwordEncoder.encode("admin"))
                                .uuid(UUID.randomUUID())
                                .role(User.RoleType.ADMIN)
                                .build();
            logger.debug("Super user created!");
            userRepo.save(user);
        }
    }
}
