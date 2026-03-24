package com.grenade.main.service;

import org.springframework.stereotype.Service;

import com.grenade.main.entity.User;
import com.grenade.main.repo.UserRepo;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
// Used to prevent bean loop
    private final UserRepo userRepo;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User loadUserByUuid(UUID userUuid) throws EntityNotFoundException{
        return userRepo.findByUuid(userUuid)
            .orElseThrow(() -> new EntityNotFoundException("User not found with uuid: "+ userUuid));
    }
    
}
