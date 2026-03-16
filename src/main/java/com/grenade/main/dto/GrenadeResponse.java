package com.grenade.main.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.grenade.main.entity.Grenade;

import lombok.Builder;

@Builder
public record GrenadeResponse(
    UUID uuid,
    String name,
    Grenade.MapType map,
    Grenade.GrenadeType grenadeType,
    String side,
    String media,
    String thumbnail,
    String command,
    List<String> buttons,
    String speed,
    String description,
    boolean approved,
    Long stars,
    String authorName,
    boolean likedByMe,
    boolean isReady,
    LocalDateTime createdAt
) {
}