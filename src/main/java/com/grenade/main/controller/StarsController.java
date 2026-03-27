package com.grenade.main.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grenade.main.service.StarsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/star")
@AllArgsConstructor
public class StarsController {
    
    private StarsService starsService;
    
    private final Logger logger = LoggerFactory.getLogger(StarsController.class);

    private static final String api = "/api/star";

    @Operation(summary = "Star grenade")
    @Tag(name = "user")
    @PostMapping("/{uuid}")
    private ResponseEntity<?> star(@PathVariable @NonNull UUID uuid){
        logger.info("POST ",api,uuid);
        boolean liked = starsService.toggleStar(uuid);
        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        return ResponseEntity.ok(response);
    }
}
