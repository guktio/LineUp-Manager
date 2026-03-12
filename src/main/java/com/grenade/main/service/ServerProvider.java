package com.grenade.main.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.grenade.main.repo.UserRepo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ServerProvider {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserRepo userRepo;

    private final String secret = "csWW";

    // public ServerProvider( @Value("${server.key}") String secret){
    //     this.secret = secret;
    // }

    public boolean validateKey(String key){
        String[] parts = key.split("-");
        logger.info(parts[0]);
        String userId = parts[1];
        Boolean exists = userRepo.existsBySteamId(userId);
        if (parts[0].startsWith(secret) && exists) {
            logger.info("succes");

            return true;
        } 
        return false;
    }
    
}
