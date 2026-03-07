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
import com.grenade.main.entity.User;
import com.grenade.main.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "token")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    private static final String api = "/api/users";
    
    @Operation(summary = "Update user")
    @Tag(name = "admin")
    @PutMapping("/{id}")
    public UserDTO update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        return userService.update(id, user);
    }

    @Operation(summary = "Create user")
    @PreAuthorize("hasRole('ADMIN')")
    @Tag(name = "admin")
    @PostMapping
    public User create(@RequestBody User user) {
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
    
    @Operation(summary = "Get user by username")
    @Tag(name = "public")
    @GetMapping("/{username}")
    public UserDTO getUserByUsername(@PathVariable String username) {
        logger.info("GET {}/{}", api,username);
        return userService.findByUsername(username);
    }

    @Operation(summary = "Get logged user")
    @Tag(name = "user")
    @GetMapping("/me")
    public UserDTO currentUser() {
        logger.info("GET {}/me",api);
        return userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }
    
    @Operation(summary = "Delete user")
    @Tag(name = "user")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        logger.info("DELETE {}/{}",api,id);
        userService.delete(Objects.requireNonNull(id));
    }
}