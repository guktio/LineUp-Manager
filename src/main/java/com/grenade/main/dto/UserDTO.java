package com.grenade.main.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.grenade.main.entity.User.RoleType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {
    private String username;
    private UUID uuid;
    private RoleType role;
    private LocalDateTime createdAt;
}
