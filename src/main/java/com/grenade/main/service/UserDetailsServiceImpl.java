package com.grenade.main.service;

import org.springframework.stereotype.Service;

import com.grenade.main.entity.User;
import com.grenade.main.repo.UserRepo;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
// Used to prevent bean loop
    private final UserRepo userRepo;

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);


    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.warn("Dont need to be called;");
        logger.debug("User loaded with username: {}",username );
        return userRepo.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User loadUserByUuid(UUID userUuid) throws EntityNotFoundException{
        logger.debug("User loaded with uuid: {}",userUuid );
        return userRepo.findByUuid(userUuid)
            .orElseThrow(() -> new EntityNotFoundException("User not found with uuid: "+ userUuid));
    }
    
}
