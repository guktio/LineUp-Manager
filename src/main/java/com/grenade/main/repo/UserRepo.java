package com.grenade.main.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grenade.main.entity.User;

@Repository
public interface UserRepo extends RepoBase<User, UUID> {
    Optional<User> findByUsername(String username);
    boolean existsByUuid(UUID uuid);
    boolean existsByEmail(String email);
    @Query("SELECT u FROM User u JOIN u.steamProfile sp WHERE sp.steamId = :steamId")
    Optional<User> findBySteamId(@Param("steamId") String steamId);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u JOIN u.steamProfile sp WHERE sp.steamId = :steamId")
    boolean existsBySteamId(@Param("steamId") String steamId);
}
