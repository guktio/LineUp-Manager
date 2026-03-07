// package com.grenade.main.grenade;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.transaction.annotation.Transactional;

// import com.grenade.main.service.GrenadeService;

// @SpringBootTest
// @AutoConfigureMockMvc
// @Transactional
// public class GrenadeControllerIntegrationTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private GrenadeService grenadeService;

//     @Test
//     void createGrenade_thenFindInDB() throws Exception {
//         String req_grenade = """
                // {
                //     "name":"test",
                //     "map":"DUST2",
                //     "grenadeType":"SMOKE",
                //     "side":"CT",
                //     "command":"commandTest",
                //     "movement":"movement",
                //     "strength":"strength",
                //     "author":1,
                // }
//                 """;
//     }
// }
