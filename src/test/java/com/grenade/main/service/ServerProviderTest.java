package com.grenade.main.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ServerProviderTest {

    @Autowired
    private ServerProvider serverProvider;
    
    @Autowired
    private SteamService steamService;

    @Test
    void ShouldValidateKey() {
        steamService.createSteamUser("76561198848703847");
        Boolean result = serverProvider.validateKey("TestKeyString-76561198848703847");
        assertThat(result).isTrue();
    }

    @Test
    void ShouldNotValidateKey_WrongSecret() {
        steamService.createSteamUser("76561198848703847");
        Boolean result = serverProvider.validateKey("CorruptedKey-76561198848703847");
        assertThat(result).isFalse();
    }

    @Test
    void ShouldNotValidateKey_EmptyKey() {
        steamService.createSteamUser("76561198848703847");
        Boolean result = serverProvider.validateKey("");
        assertThat(result).isFalse();
    }

    @Test
    void shouldNotValidateKey_invalidFormat() {
        boolean result = serverProvider.validateKey("invalidkey");

        assertThat(result).isFalse();
    }

    @Test
    void shouldNotValidateKey_userNotExists() {
        boolean result = serverProvider.validateKey("TestKeyString-999");

        assertThat(result).isFalse();
    }
}
