package com.grenade.main.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.grenade.main.entity.User.RoleType;

import lombok.Builder;

@Builder
public record UserDTO(
    String username,
    String email,
    UUID uuid,
    RoleType role,
    SteamProfileDTO profile,
    LocalDateTime createdAt
) {}