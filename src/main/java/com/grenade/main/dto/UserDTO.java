package com.grenade.main.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.grenade.main.entity.User.RoleType;

import lombok.Builder;

@Builder
public record UserDTO(
    String username,
    String steamId,
    UUID uuid,
    RoleType role,
    LocalDateTime createdAt
) {}