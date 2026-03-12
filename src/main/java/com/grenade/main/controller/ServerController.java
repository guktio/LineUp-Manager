package com.grenade.main.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grenade.main.dto.AuthResponse;
import com.grenade.main.dto.GrenadeRequest;
import com.grenade.main.dto.GrenadeResponse;
import com.grenade.main.dto.ServerUserDTO;
import com.grenade.main.entity.User;
import com.grenade.main.service.GrenadeService;
import com.grenade.main.service.SteamService;
import com.grenade.main.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
@PreAuthorize("hasRole('GAME_SERVER')")
public class ServerController {

    private final GrenadeService grenadeService;
    private final UserService userService;
    private final SteamService steamService;

    private static final Logger logger = LoggerFactory.getLogger(ServerController.class);

    @PostMapping("/lineup/new")
    public ResponseEntity<GrenadeResponse> create(@RequestBody GrenadeRequest grenade){
        String[] parts = SecurityContextHolder.getContext().getAuthentication().getName().split("-");
        User user = userService.getBySteamId(parts[1]);
         Authentication auth = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities());
        
        SecurityContextHolder.getContext().setAuthentication(auth);
        logger.info(grenade.toString());
        return new ResponseEntity<>(grenadeService.create(grenade), HttpStatus.OK);
    }

    @PostMapping("/user/steamId")
    public ResponseEntity<Boolean> isSteamID(@RequestBody String steamId){
        logger.info(steamId);
        return new ResponseEntity<>(userService.isUserExist(steamId), HttpStatus.OK);
    }

    @PostMapping("/user/auth")
    public ResponseEntity<AuthResponse> authUser(@RequestBody ServerUserDTO steamId){
        logger.info("POST /api/game/user/auth steamID={}",steamId);
        return new ResponseEntity<AuthResponse>(steamService.loginWithSteam(steamId), HttpStatus.OK);
    }
}
