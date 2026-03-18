package com.grenade.main.service;

import java.util.Objects;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.grenade.main.dto.UserDTO;
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

    public User create(User user){
        if (userRepo.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("User already exists");
        }

        if (userRepo.existsBySteamId(user.getSteamId())) {
            throw new RuntimeException("User with steamId already exists");
        }
        

        User.UserBuilder usr = User.builder()
            .username(user.getUsername())
            .steamId(user.getSteamId());

        if (user.getPassword() != null && !user.getPassword().isBlank()){
            usr.password(passwordEncoder.encode(user.getPassword()));
        }


        if (user.getSteamProfile() != null && !user.getSteamProfile().equals(null)){
            usr.steamProfile(user.getSteamProfile());
        }

        User saved = userRepo.save(Objects.requireNonNull(usr.build(), "User builder returns null"));
        return saved;
    }

    public boolean isUserExist(String steamId) {
        return userRepo.existsBySteamId(steamId);
    }

    public User getBySteamId(String steamId){
        return userRepo.findBySteamId(steamId).orElseThrow(() -> new EntityNotFoundException("User was not found with steamId "+ steamId));
    }

    public User update(Long id, User user) {
        user.setId(id);
        User saved = userRepo.save(user);
        return saved;
    }

    @Override
    public UserDTO toDTO(User user){
        UserDTO userDTO = UserDTO.builder()
                        .username(user.getUsername())
                        .steamId(user.getSteamId())
                        .uuid(user.getUuid())
                        .role(user.getRole())
                        .createdAt(user.getCreatedAt())
                        .build();
        return userDTO;
    }

    public UserDTO findByUsername(String username) {
        return userRepo.findByUsername(username)
                .map(this::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
    }
}
