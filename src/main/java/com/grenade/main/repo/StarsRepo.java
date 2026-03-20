package com.grenade.main.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grenade.main.entity.Stars;

@Repository
public interface StarsRepo extends JpaRepository<Stars,Long>{
    Optional<Stars> findByUserUuidAndGrenadeUuid(UUID userId, UUID grenadeId);
    Long countByGrenadeUuid(UUID grenadeId);
    boolean existsByUserIdAndGrenadeId(Long userId, Long grenadeId);
    Stars findByUserId(Long id);
}