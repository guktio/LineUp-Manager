package com.grenade.main.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grenade.main.entity.SteamProfile;

@Repository
public interface SteamRepo extends JpaRepository<SteamProfile, Long> {
    
}
