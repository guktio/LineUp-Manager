package com.grenade.main.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.grenade.main.entity.User;

@Repository
public interface UserRepo extends RepoBase<User, UUID> {
    Optional<User> findByUsername(String username);
    boolean existsByUuid(UUID uuid);
    boolean existsByEmail(String email);
    Optional<User> findBySteamId(String steamId);
    boolean existsBySteamId(String steamId);
}
