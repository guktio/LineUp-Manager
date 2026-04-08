package com.grenade.main.controller;

import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.grenade.main.dto.PageDTO;
import com.grenade.main.dto.UserDTO;
import com.grenade.main.dto.UserRequest;
import com.grenade.main.entity.User;
import com.grenade.main.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "token")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    private static String api = "/api/users";

    @Operation(summary = "Get user by username")
    @Tag(name = "public")
    @GetMapping("/{uuid}")
    public UserDTO getUserByUuid(@PathVariable String uuid) {
        logger.info("GET {}/{}", api, uuid);
        return userService.findByUuid(UUID.fromString(uuid));
    }
    
    @Operation(summary = "Update user")
    @Tag(name = "user")
    @PutMapping("/{uuid}")
    public User update(@PathVariable UUID uuid, @RequestBody UserRequest user) {
        logger.info("PUT {}/{}",api,uuid);
        return userService.update(uuid, user);
    }

    @Operation(summary = "Create user")
    @Tag(name = "admin")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public User create(@RequestBody UserRequest user) {
        logger.info("POST {}",api);
        return userService.create(user);
    }

    @Operation(summary = "Get users page")
    @Tag(name = "public")
    @GetMapping()
    public PageDTO<UserDTO> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        logger.info("GET {}",api);
        return userService.getAll(PageRequest.of(page, size));
    }

    @Operation(summary = "Get logged user")
    @Tag(name = "user")
    @GetMapping("/me")
    public UserDTO currentUser() {
        logger.info("GET {}/me",api);
        UUID userUuid = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUuid();
        return userService.findByUuid(userUuid);
    }

    @Operation(summary = "Delete user")
    @Tag(name = "user")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        logger.info("DELETE {}/{}",api,id);
        userService.deleteUser(Objects.requireNonNull(id));
    }
}