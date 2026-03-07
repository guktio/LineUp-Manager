// package com.grenade.main.user;

// import com.grenade.main.entity.User;
// import com.grenade.main.service.UserService;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.transaction.annotation.Transactional;

// import static org.junit.jupiter.api.Assertions.assertNotEquals;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @SpringBootTest
// @AutoConfigureMockMvc
// @Transactional
// public class UserControllerIntegrationTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private UserService userService;

//     @SuppressWarnings("null")
//     @Test
//     void createUser_ThenFindInDB() throws Exception {
//         String req = """
//             {
//                 "username": "alex",
//                 "password": "plain123"
//             }
//         """;

//         mockMvc.perform(post("/api/users")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(req))
//                 .andExpect(status().isOk());

//         User user = userService.findByUsername("alex");
//         System.out.println("User found in DB: " + user);

//         assertNotEquals("plain123", user.getPassword());
//     }
// }
