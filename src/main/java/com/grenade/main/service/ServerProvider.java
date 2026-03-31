package com.grenade.main.service;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import com.grenade.main.repo.UserRepo;

@Component
public class ServerProvider {

    private UserRepo userRepo;

    private String secret;

    public ServerProvider( @Value("${server.key}") 
                            String secret,
                            UserRepo userRepo){
        this.secret = secret;
        this.userRepo = userRepo;
    }

    public boolean validateKey(String key){
        String[] parts = key.split("-");
        if (parts.length < 2) return false;
        String userId = parts[1];
        Boolean exists = userRepo.existsBySteamId(userId);
        if (parts[0].startsWith(secret) && exists) {
            return true;
        } 
        return false;
    }
}
