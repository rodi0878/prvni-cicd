package com.bezkoder.springjwt;

import com.bezkoder.springjwt.payload.request.LoginRequest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class RestMockTests {

    private static class LoginResponse {

        //id":1,"username":"user","email":"user@domain.com","roles":["ROLE_ADMIN","ROLE_USER"],"tokenType":"Bearer","accessToken":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNjQxMDY1MzI2LCJleHAiOjE2NDExNTE3MjZ9.0C0hSqEbwBBMqkIzSsc0er4OjwK_xl-EmDjQUjYegDXCL0eTRDPIyd6-y20d9p5av54SSh4CI8AKpdPIUV2HRg"}    }
        private int id;
        private String username;
        private String email;
        private List<String> roles;
        private String tokenType;
        private String accessToken;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        @Override
        public String toString() {
            return "LoginResponse{" + "id=" + id + ", username=" + username + ", email=" + email + ", roles=" + roles + ", tokenType=" + tokenType + ", accessToken=" + accessToken + '}';
        }

    }

    @Autowired
    WebApplicationContext context;

    @Test
    public void mockMvcWebTestClientTest() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("password");

        WebTestClient wtc = MockMvcWebTestClient.bindToApplicationContext(this.context).apply(springSecurity()).configureClient().build();
        ResponseSpec o = wtc.post().uri("/api/auth/signin").contentType(MediaType.APPLICATION_JSON).body(Mono.just(request), LoginRequest.class).exchange();
        LoginResponse lr = o.expectStatus().isOk().returnResult(LoginResponse.class).getResponseBody().blockFirst();
        System.out.println(lr);

        wtc.get().uri("/api/test/user").header("Authorization", "Bearer " + lr.getAccessToken()).exchange().expectStatus().isOk();
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testMockUser() {
        WebTestClient wtc = MockMvcWebTestClient.bindToApplicationContext(this.context).apply(springSecurity()).configureClient().build();
        wtc.get().uri("/api/test/user").exchange().expectStatus().isOk();
    }

    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testMockUserWithMockMvc() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/test/user")).andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());
    }
}
