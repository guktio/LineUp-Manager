package com.grenade.main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class AuthRequest {
    private String username;
    private String email;
    private String password;
}
