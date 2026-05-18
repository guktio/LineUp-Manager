package com.grenade.main.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grenade.main.entity.Grenade;
import com.grenade.main.entity.Stars;
import com.grenade.main.entity.User;
import com.grenade.main.repo.GrenadeRepo;
import com.grenade.main.repo.StarsRepo;
import com.grenade.main.repo.UserRepo;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class StarsService {

    private StarsRepo starsRepo;
    private UserRepo userRepo;
    private GrenadeRepo grenadeRepo;
    private static final Logger logger = LoggerFactory.getLogger(StarsService.class);

    @Transactional
    public boolean toggleStar(@NonNull UUID grUuid, UUID userUuid){
        User user = userRepo.findByUuid(userUuid).orElseThrow(() -> 
            new EntityNotFoundException("User not found with uuid: " + userUuid));
        Grenade grenade = grenadeRepo.findByUuid(grUuid).orElseThrow(() -> 
            new EntityNotFoundException("Grenade with uuid "+grUuid+"not found"));
        Optional<Stars> exists = starsRepo.findByUserUuidAndGrenadeUuid(user.getUuid(),grUuid);
        if(exists.isPresent()) {
            logger.info("User: {} unstared lineup: {};",user.getUuid(),grenade.getUuid());
            exists.ifPresent(starsRepo::delete);
            grenadeRepo.decreaseStars(grUuid);
            return false;
        } else {
            logger.info("User: {} stared lineup: {};",user.getUuid(),grenade.getUuid());
            Stars star = new Stars();
            star.setGrenade(grenade);
            star.setUser(user);
            grenadeRepo.increaseStars(grUuid);
            starsRepo.save(star);
            return true;
        }
    }
}
