package com.grenade.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.grenade.main.dto.UserRequest;
import com.grenade.main.entity.User;
import com.grenade.main.repo.UserRepo;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class UserServiceTest {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private Environment env;

    @Test
    void shouldUseTestProfile() {
        assertThat(env.getActiveProfiles()).contains("test");
    }
    
    @Test
    void create_shouldSaveUser_UserDoesNotExist() {
        UserRequest input = UserRequest.builder()
                .username("john")
                .password("123")
                .email("testmail@mail.com")
                .build();

        User result = userService.create(input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUsername()).isEqualTo("john");

        assertThat(passwordEncoder.matches("123", result.getPassword())).isTrue();

        User fromDb = userRepo.findByUuid(result.getUuid()).orElseThrow();
        assertThat(fromDb.getUsername()).isEqualTo("john");
    }

    @Test
    void create_shouldNotSaveUser_UserDoesExist() {
        UserRequest input = UserRequest.builder()
                .username("john")
                .password("123")
                .email("testmail@mail.com")
                .build();
        userService.create(input);
        
        UserRequest input2 = UserRequest.builder()
                .username("johnasdasd")
                .password("123123123123123")
                .email("testmail@mail.com")
                .build();
        System.out.println(input2.toString());
        assertThrows(RuntimeException.class, () -> {
            userService.create(input2);
        });
    }

    //need to add fake context user
    // @Test
    // void create_shouldUpdate_ExistsUser() {
    //     User appUser = new User();
    //     appUser.setUsername("john");
    //     appUser.setPassword("123");
    //     appUser.setEmail("testmail@mail.com");

    //     SecurityContextHolder.getContext().setAuthentication(
    //             new UsernamePasswordAuthenticationToken(appUser, null, List.of())
    //     );

    //     UserRequest input = UserRequest.builder()
    //             .username("john")
    //             .password("123")
    //             .email("testmail@mail.com")
    //             .build();
    //     User saved = userService.create(input);

    //     UserRequest input2 = UserRequest.builder()
    //             .username("john2")
    //             .password("123")
    //             .email("testmail@mail.com")
    //             .build();
    //     User result = userService.update(saved.getUuid(), input2);

    //     User fromDb = userRepo.findByUuid(result.getUuid()).orElseThrow();
    //     assertThat(fromDb.getUsername()).isEqualTo("john2");

    //     SecurityContextHolder.clearContext();
    // }
}
