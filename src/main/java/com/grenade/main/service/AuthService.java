package com.grenade.main.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.grenade.main.dto.AuthRequest;
import com.grenade.main.dto.AuthResponse;
import com.grenade.main.dto.UserRequest;
import com.grenade.main.entity.User;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder; 

    private final JwtProvider jwtProvider;

    private final AuthenticationManager authenticationManager;

    private final static Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthResponse register(AuthRequest authRequest) {
        UserRequest.UserRequestBuilder user = UserRequest
                            .builder()
                            .username(authRequest.getUsername())
                            .email(authRequest.getEmail())
                            .password(authRequest.getPassword());
        if(authRequest.getPassword() != null && authRequest.getPassword().equals(null)){
            user.password(passwordEncoder.encode(authRequest.getPassword()));
        }
        User created = userService.create(user.build());

        String token = jwtProvider.generateToken(created.getUuid());

        logger.info("User registered {}", created.toString());
        return new AuthResponse(userService.toDTO(created), token);
    }

    public AuthResponse login(AuthRequest authRequest) {
        try{
            UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());

            Authentication auth = authenticationManager.authenticate(authToken);

            SecurityContextHolder.getContext().setAuthentication(auth);

            UUID userUuid = ((User) auth.getPrincipal()).getUuid(); 

            String token = jwtProvider.generateToken(userUuid);
            
            logger.info("User {} logged in.",auth.getName());
            return new AuthResponse(userService.findByUuid(UUID.fromString(jwtProvider.getUuidFromToken(token))), token);

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("User not found");
        }
    }
}