package com.grenade.main.service;

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
//Not used now
    public AuthResponse register(AuthRequest authRequest) {
        User user = new User();
        user.setUsername(authRequest.getUsername());
        if(authRequest.getPassword() != null && authRequest.getPassword().equals(null)){
            user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        }

        userService.create(user);
        String token = jwtProvider.generateToken(user.getUsername());
        logger.info("User registered {}",user.toString());
        return new AuthResponse(userService.findByUsername(jwtProvider.getUsernameFromToken(token)), token);
    }
//Not used now
    public AuthResponse login(AuthRequest authRequest) {
        try{
            UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());

            Authentication auth = authenticationManager.authenticate(authToken);

            SecurityContextHolder.getContext().setAuthentication(auth);

            String token = jwtProvider.generateToken(auth.getName());
            logger.info("User {} logged in.",auth.getName());
            return new AuthResponse(userService.findByUsername(jwtProvider.getUsernameFromToken(token)), token);

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("User not found");
        }
    }
}