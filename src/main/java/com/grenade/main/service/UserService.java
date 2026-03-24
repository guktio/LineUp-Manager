package com.grenade.main.service;

import java.util.Objects;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.grenade.main.dto.UserDTO;
import com.grenade.main.dto.UserRequest;
import com.grenade.main.entity.User;
import com.grenade.main.repo.UserRepo;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService extends ServiceBase<User, UserDTO, UUID, UserRepo>{

    private final UserRepo userRepo;

    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepo, BCryptPasswordEncoder passwordEncoder){
        super(userRepo);
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public User create(UserRequest user){
        if (userRepo.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email taken by another account");
        }

        if (user.getSteamId() != null) {
            if (userRepo.existsBySteamId(user.getSteamId())) {
                throw new RuntimeException("User with steamId already exists");
            }
        }

        User.UserBuilder usr = User.builder()
            .username(user.getUsername())
            .email(user.getEmail())
            .steamId(user.getSteamId());

        if (user.getPassword() != null && !user.getPassword().isBlank()){
            usr.password(passwordEncoder.encode(user.getPassword()));
        }


        // if (user.getSteamProfile() != null && !user.getSteamProfile().equals(null)){
        //     usr.steamProfile(user.getSteamProfile());
        // }

        User saved = userRepo.save(Objects.requireNonNull(usr.build(), "User builder returns null"));
        return saved;
    }

    public boolean isUserExist(String steamId) {
        return userRepo.existsBySteamId(steamId);
    }

    public User getBySteamId(String steamId){
        return userRepo.findBySteamId(steamId).orElseThrow(() -> new EntityNotFoundException("User was not found with steamId "+ steamId));
    }

    @SuppressWarnings("null")
    public User fullUpdate(UUID uuid, User user) {
        UUID userUuid = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUuid();
        User existing = userRepo.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("User not found with uuid: "+ uuid.toString()));

        boolean isOwner = userUuid.equals(existing.getUuid());
        boolean isAdmin = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRole().equals("ROLE_ADMIN");
        if (!isOwner && !isAdmin){
            throw new AccessDeniedException("You can only update your own profile");
        }

        User newUser = User.builder()
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .steamId(user.getSteamId())
                            .role(user.getRole())
                            .steamProfile(user.getSteamProfile())
                            .uuid(user.getUuid())
                            .build();
        return userRepo.save(newUser);
    }

    @Override
    public UserDTO toDTO(User user){
        UserDTO userDTO = UserDTO.builder()
                        .username(user.getUsername())
                        .steamId(user.getSteamId())
                        .email(user.getEmail())
                        .uuid(user.getUuid())
                        .role(user.getRole())
                        .createdAt(user.getCreatedAt())
                        .build();
        return userDTO;
    }

    public UserDTO findByUuid(UUID uuid){
        return userRepo.findByUuid(uuid)
                .map(this::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("User not found with uuid: "+uuid));
    }
}
