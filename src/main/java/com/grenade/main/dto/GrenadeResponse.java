package com.grenade.main.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.grenade.main.entity.Grenade;

import lombok.Builder;

@Builder
public record GrenadeResponse(
    UUID uuid,
    String name,
    Grenade.MapType map,
    Grenade.GrenadeType grenadeType,
    Grenade.Side side,
    String media,
    String thumbnail,
    String command,
    String movement,
    String strength,
    String description,
    boolean approved,
    Long stars,
    String authorName,
    boolean likedByMe,
    LocalDateTime createdAt
) {
}