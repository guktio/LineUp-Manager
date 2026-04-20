package com.grenade.main.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grenade.main.dto.SteamPlayerSummary;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SteamApiClient {
    
    @Value("${STEAM_API_KEY:}")
    private String apiKey;
    
    private static final String BASE_URL = "https://api.steampowered.com";
    
    private final HttpClient client = HttpClient.newBuilder().build();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ObjectMapper mapper = new ObjectMapper();


    public SteamPlayerSummary getPlayerSummaries(String steamId){
        String url = String.format(
            "%s/ISteamUser/GetPlayerSummaries/v0002/?key=%s&steamids=%s",
            BASE_URL, apiKey, steamId
        );
        logger.debug("Steam URL "+url);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();
        
        try {
            HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
            );
            logger.debug(response.toString()); 
            JsonNode players = mapper.readTree(response.body())
                    .path("response")
                    .path("players");
            if(!players.isArray() || players.isEmpty()){
                throw new RuntimeException("Players not found");
            }
            JsonNode player = players.get(0);
            SteamPlayerSummary steamInfo = new SteamPlayerSummary(player.path("personaname").asText(),
                                                                player.path("profileurl").asText(),
                                                            steamId);
            logger.debug("Player summaries: {}", steamInfo.toString());
            return steamInfo;
        } catch (InterruptedException e) {
            logger.error("Thread is waiting", e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.error("Error", e);
            throw new RuntimeException(e);
        }
    }

    //TODO: add parsing to dto
    public String getSteamNewsForApp(Long appid, Long count){
            String url = String.format(
            "%s/ISteamNews/GetNewsForApp/v2/?appid=%s&count=%s",
            BASE_URL, appid, count
        );
        logger.debug("Steam URL {}", url);
        HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .GET()
                            .build();
        try {
            HttpResponse<String> response = client.send(
                request, 
                HttpResponse.BodyHandlers.ofString()
            );
            if (response.statusCode() != 200) {
                throw new RuntimeException(
                    "Steam API returned status " + response.statusCode()
                );
            }
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch Steam news for appId=" + appid, e);
        }
    }
}
