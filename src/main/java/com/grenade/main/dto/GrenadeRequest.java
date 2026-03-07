package com.grenade.main.dto;

import com.grenade.main.entity.Grenade;

import lombok.Builder;

@Builder
public record GrenadeRequest(
    String name,
    Grenade.MapType map,
    Grenade.GrenadeType grenadeType,
    Grenade.Side side,
    String media,
    String command,
    String movement,
    String strength,
    String description

) {
}