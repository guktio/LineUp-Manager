package com.grenade.main.controller;


import org.slf4j.LoggerFactory;

import java.util.Map;

import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grenade.main.dto.AuthRequest;
import com.grenade.main.dto.AuthResponse;
import com.grenade.main.service.AuthService;
import com.grenade.main.service.SteamService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@Tag(name = "auth")
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SteamService steamService;
    private final Logger logger = LoggerFactory.getLogger(AuthController.class); 
    private static final String api = "/api/auth";

    @Operation(summary = "Registration for user")
    @ApiResponses(
        @ApiResponse(responseCode = "200", description = "User registrated succesfully")
    )
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest authRequest) {
        logger.info("POST {}/register",api);
        return ResponseEntity.ok(authService.register(authRequest));
    }

    @Operation(summary = "Login for user")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        logger.info("POST {}/login",api);
        return ResponseEntity.ok(authService.login(authRequest));
    }

    @Operation(summary = "Sign in via steam")
    @PostMapping("/steam")
    public ResponseEntity<AuthResponse> loginViaSteam(@RequestBody Map<String,String> params) {
        logger.info("GET {}/steam", api);
        return ResponseEntity.ok(steamService.steamResponse(params));
    }
}
