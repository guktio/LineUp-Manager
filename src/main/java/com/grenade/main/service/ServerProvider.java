package com.grenade.main.service;

import org.springframework.stereotype.Component;

import com.grenade.main.repo.UserRepo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ServerProvider {

    private final UserRepo userRepo;

    private final String secret = "csWW";

    // public ServerProvider( @Value("${server.key}") String secret){
    //     this.secret = secret;
    // }

    public boolean validateKey(String key){
        String[] parts = key.split("-");
        String userId = parts[1];
        Boolean exists = userRepo.existsBySteamId(userId);
        if (parts[0].startsWith(secret) && exists) {
            return true;
        } 
        return false;
    }
    
}
