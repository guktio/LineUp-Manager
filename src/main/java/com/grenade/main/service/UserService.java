package com.grenade.main.service;

import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grenade.main.dto.SteamProfileDTO;
import com.grenade.main.dto.UserDTO;
import com.grenade.main.dto.UserRequest;
import com.grenade.main.entity.User;
import com.grenade.main.entity.User.RoleType;
import com.grenade.main.repo.UserRepo;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService extends ServiceBase<User, UserDTO, UUID, UserRepo>{

    private final UserRepo userRepo;

    private final BCryptPasswordEncoder passwordEncoder;

    private final Logger logger = LoggerFactory.getLogger(getClass());

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
            .email(user.getEmail());

        if (user.getPassword() != null && !user.getPassword().isBlank()){
            usr.password(passwordEncoder.encode(user.getPassword()));
        }

        User saved = userRepo.save(Objects.requireNonNull(usr.build(), "User builder returns null"));
        logger.debug("User created: {}",saved.toString());
        return saved;
    }

    public boolean isUserExist(String steamId) {
        logger.debug("Is user exists(?): {}",steamId);
        return userRepo.existsBySteamId(steamId);
    }

    public User getBySteamId(String steamId){
        logger.debug("Load user by Steam Id: {}",steamId);
        return userRepo.findBySteamId(steamId).orElseThrow(() -> new EntityNotFoundException("User was not found with steamId "+ steamId));
    }

    @Transactional
    public User update(UUID uuid, UserRequest user) {
        User userContext = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID userUuid = userContext.getUuid();
        User existing = userRepo.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("User not found with uuid: "+ uuid.toString()));

        boolean isOwner = userUuid.equals(existing.getUuid());
        boolean isAdmin = userContext.getRole().equals(RoleType.ADMIN);
        if (!isOwner && !isAdmin){
            throw new AccessDeniedException("You can only update your own profile");
        }

        if (user.getUsername() != null) {
            existing.setUsername(user.getUsername());
        }

        if (user.getEmail() != null) {
            existing.setEmail(user.getEmail());
        }

        // if (existing.getSteamProfile() != null) {
        //     existing.setSteamProfile(existing.getSteamProfile());
        // }

        // if (isAdmin && existing.getRole() != null) {
        //     existing.setRole(existing.getRole());
        // }
        logger.debug("User with uuid: {} updated with fields: {}", uuid, existing);
        return existing;
    }

    @Override
    public UserDTO toDTO(User user){
        UserDTO.UserDTOBuilder userDTO = UserDTO.builder()
                        .uuid(user.getUuid())
                        .username(user.getUsername())
                        .role(user.getRole())
                        .email(user.getEmail())
                        .createdAt(user.getCreatedAt());
        if (user.getSteamProfile() != null) {
            SteamProfileDTO steamProfileDTO = new SteamProfileDTO();
            steamProfileDTO.setSteamId(user.getSteamProfile().getSteamId());
            steamProfileDTO.setPersonaname(user.getSteamProfile().getPersonaname());
            steamProfileDTO.setProfileurl(user.getSteamProfile().getProfileurl());
            userDTO.profile(steamProfileDTO);
        }
        return userDTO.build();
    }

    public UserDTO findByUuid(UUID uuid){
        logger.debug("Find user with uuid: {}", uuid);
        return userRepo.findByUuid(uuid)
                .map(this::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("User not found with uuid: "+uuid));
    }

    public void deleteUser(UUID uuid){
        if(uuid == null) return;
        Authentication user = SecurityContextHolder.getContext().getAuthentication();
        User existing = userRepo.findByUuid(uuid)
            .orElseThrow(() -> new EntityNotFoundException("Entity not found: " + uuid));
        boolean isOwner = existing.getUsername().equals(user.getName());
        boolean isAdmin = user.getAuthorities().stream()
        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if(isOwner || isAdmin){
            delete(uuid);
        }
    }
}