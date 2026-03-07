package com.grenade.main.service;

import org.springframework.stereotype.Service;

import com.grenade.main.entity.User;
import com.grenade.main.repo.UserRepo;

import lombok.AllArgsConstructor;

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
    
}
