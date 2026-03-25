package com.grenade.main.dto;

import lombok.Builder;

@Builder
public record ServerUserDTO(
    String userId
) {}
