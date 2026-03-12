package com.grenade.main.dto;

import java.util.List;

import com.grenade.main.entity.Grenade;

import lombok.Builder;

@Builder
public record GrenadeRequest(
    String name,
    String command,
    Grenade.MapType map,
    Grenade.GrenadeType grenadeType,
    Grenade.Side side,
    String speed,
    List<String> buttons, 
    String media,
    String description
) {
}
