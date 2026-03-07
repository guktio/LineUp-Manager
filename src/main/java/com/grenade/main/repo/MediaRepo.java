package com.grenade.main.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.grenade.main.entity.Media;

@Repository
public interface MediaRepo extends RepoBase<Media, UUID>{
    Optional<Media> findByUuid(UUID uuid);
}
