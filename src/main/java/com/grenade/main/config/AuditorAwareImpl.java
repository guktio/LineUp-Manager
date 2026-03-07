package com.grenade.main.config;


import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.grenade.main.entity.User;
import com.grenade.main.repo.UserRepo;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class AuditorAwareImpl implements AuditorAware<User> {

    private final UserRepo userRepo;
    
    @Override
    @SuppressWarnings("null")
    public Optional<User> getCurrentAuditor() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUsername(username);
    }
}
