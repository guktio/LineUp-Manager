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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grenade.main.dto.SteamPlayerSummary;
import com.grenade.main.dto.SteamProfileDTO;
import com.grenade.main.dto.UserDTO;
import com.grenade.main.entity.SteamProfile;
import com.grenade.main.entity.User;
import com.grenade.main.exception.SteamAuthException;
import com.grenade.main.repo.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SteamService {
    
    private final SteamApiClient steamApi;

    private final HttpClient client = HttpClient.newBuilder().build();
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserRepo userRepo;

    public String validateSteamOpenIdResponse(Map<String, String> openIdParams) {
        String loginUrl = "https://steamcommunity.com/openid/login";
        Map<String, String> copy = openIdParams;
    
        copy.put("openid.mode", "check_authentication");

        StringBuilder form = new StringBuilder();

        for (Map.Entry<String, String> entry : copy.entrySet()) {
            form.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
            .append("=")
            .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
            .append("&");
        }

        logger.debug(form.toString());

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

            String claimedId = copy.get("openid.claimed_id");

            if (claimedId != null && isValid(response.body())) {
                steamId = claimedId.substring(claimedId.lastIndexOf("/") + 1);
            }
            if (steamId == null || steamId.isBlank()){
                throw new SteamAuthException("SteamId is null.");
            } 
        } catch (Exception e) {
            logger.error("message: {}", e);
            throw new BadCredentialsException("Error in steam login", e);
        }
        return steamId;
    }

    private boolean isValid(String body) {
        logger.debug("Steam response: \n{} ",body);
        for (String line : body.split("\n")) {
            line = line.trim();
            if (line.equals("is_valid:true")) {
                return true;
            }
        }
        return false;
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

    @Transactional
    public User createSteamUser(String steamId){
        SteamPlayerSummary playerInfo = steamApi.getPlayerSummaries(steamId);
		logger.info("Created Steam User with name {}" ,playerInfo.personaname());
        User user = User.builder().username(playerInfo.personaname()).build();
        SteamProfile profile = SteamProfile.builder()
            .steamId(steamId)
            .personaname(playerInfo.personaname())
            .profileurl(playerInfo.profileurl())
            .user(user)
            .build();
        user.setSteamProfile(profile);
        userRepo.save(user);
        logger.debug("Created user: {}",user.toString());
        return user;
    }

    
}