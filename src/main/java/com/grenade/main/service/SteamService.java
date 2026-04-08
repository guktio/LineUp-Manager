package com.grenade.main.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grenade.main.dto.AuthResponse;
import com.grenade.main.dto.ServerUserDTO;
import com.grenade.main.dto.SteamProfileDTO;
import com.grenade.main.dto.UserDTO;
import com.grenade.main.entity.SteamProfile;
import com.grenade.main.entity.User;
import com.grenade.main.exception.SteamAuthException;
import com.grenade.main.repo.UserRepo;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SteamService {

    @Value("${STEAM_API_KEY:}")
    private String API_KEY;
    
    private static String BASE_URL = "https://api.steampowered.com";

    private HttpClient client = HttpClient.newHttpClient();
    private ObjectMapper mapper = new ObjectMapper();
    
    private final Logger logger = LoggerFactory.getLogger(SteamService.class);
    private final UserRepo userRepo;

    private final JwtProvider jwtProvider;

    public AuthResponse steamResponse(Map<String, String> openIdParams) {
        String loginUrl = "https://steamcommunity.com/openid/login";
        openIdParams.put("openid.mode", "check_authentication");

        StringBuilder form = new StringBuilder();
        for (Map.Entry<String, String> entry : openIdParams.entrySet()) {
            form.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
            .append("=")
            .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
            .append("&");
        }
        logger.info(form.toString());

        String requestBody = form.substring(0, form.length() - 1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(loginUrl))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        String steamId = null;
        try {
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            logger.info("Steam response: {}", response.body());
            String claimedId = openIdParams.get("openid.claimed_id");

            if (claimedId != null && isValidSteamResponse(response.body())) {
                steamId = claimedId.substring(claimedId.lastIndexOf("/") + 1);
            }
            if (steamId == null || steamId.isBlank()){
                throw new SteamAuthException("SteamId is null.");
            } 
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadCredentialsException("Error in steam login");
        }
        return loginWithSteam(toDTO(steamId));
    }

    private ServerUserDTO toDTO(String obj){
        ServerUserDTO dto = ServerUserDTO.builder().userId(obj).build();
        return dto;
    }


    public AuthResponse loginWithSteam(ServerUserDTO dto) {
        logger.info(dto.userId());
            User user = userRepo.findBySteamId(dto.userId())
                    .orElseGet(() -> createSteamUser(dto.userId()));
    
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities()
            );
    
            SecurityContextHolder.getContext().setAuthentication(auth);
    
            String token = jwtProvider.generateToken(user.getUuid());
    
            logger.info("User {} logged in via Steam.", user.getUsername());
            
            User exists = userRepo.findByUuid(user.getUuid()).orElseThrow(() -> new EntityNotFoundException("EntityNotFoundException"));

            return new AuthResponse(
                    toUserDTO(exists),
                    token
            );
    }

    public UserDTO toUserDTO(User user){
        UserDTO.UserDTOBuilder userDTO = UserDTO.builder()
                        .uuid(user.getUuid())
                        .username(user.getUsername())
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

    public User createSteamUser(String steamId){
        Map<String, String> playerInfo = getPlayerSummaries(steamId);
        User user = User.builder().username(playerInfo.get("personaname")).build();
        SteamProfile profile = SteamProfile.builder()
            .steamId(steamId)
            .personaname(playerInfo.get("personaname"))
            .profileurl(playerInfo.get("profileurl"))
            .user(user)
            .build();
        user.setSteamProfile(profile);
        userRepo.save(user);
        logger.debug("Created user: {}",user.toString());
        return user;
    }

    private boolean isValidSteamResponse(String body) {
        for (String line : body.split("\n")) {
            line = line.trim();
            if (line.equals("is_valid:true")) {
                return true;
            }
        }
        return false;
    }


    public Map<String, String> getPlayerSummaries(String steamId){
        String url = String.format(
            "%s/ISteamUser/GetPlayerSummaries/v0002/?key=%s&steamids=%s",
            BASE_URL, API_KEY, steamId
        );
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();
        try {
            HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
            );
            JsonNode player = mapper.readTree(response.body())
                    .path("response")
                    .path("players")
                    .get(0);
            Map<String, String> steamInfo= Map.of(
                        "personaname", player.path("personaname").asText(),
                        "profileurl", player.path("profileurl").asText());
            logger.debug("Player summaries: {}",steamInfo.toString());
            return steamInfo;
        } catch (Exception e) {
            return Map.of("error",e.getMessage());
        }
    }

    public String getCsNews(Long appid, Long count){
            String url = String.format(
            "%s/ISteamNews/GetNewsForApp/v2/?appid=%s&count=%s",
            BASE_URL, appid, count
        );
        HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .GET()
                            .build();
        try {
            HttpResponse<String> response = client.send(
                request, 
                HttpResponse.BodyHandlers.ofString()
            );
            return response.body();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}