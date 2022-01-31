package com.bezkoder.springjwt;

import com.bezkoder.springjwt.payload.request.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestEndToEndTests {

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
    @LocalServerPort
    private int port;
    
     @Test
    public void javaHttpClientTest() throws IOException, InterruptedException {
        HttpClient http = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/auth/signin")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString("{\"username\":\"user\",\"password\":\"password\"}")).build();
        String response = http.send(request, HttpResponse.BodyHandlers.ofString()).body();
        System.out.println(response);

        LoginResponse loginResponse = new ObjectMapper().readValue(response, LoginResponse.class);

        request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/test/user"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + loginResponse.accessToken)
                .header("Origin", "http://localhost:8080")
                .GET()
                .build();
        
         response = http.send(request, HttpResponse.BodyHandlers.ofString()).body(); 
         
         assertEquals("User Content.", response);
         System.out.println(response);
    }

    @Test
    public void webTestClientTest() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("password");

//        RestTemplate rest = new RestTemplate();
//        ResponseEntity<Object> response = rest.postForEntity("http://localhost:" + port + "/api/auth/signin", request, Object.class);
//        System.out.println(response);
        WebTestClient wtc = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
        ResponseSpec o = wtc.post().uri("/api/auth/signin").contentType(MediaType.APPLICATION_JSON).body(Mono.just(request), LoginRequest.class).exchange();
        LoginResponse lr = o.expectStatus().isOk().returnResult(LoginResponse.class).getResponseBody().blockFirst();
        System.out.println(lr);

        wtc.get().uri("/api/test/user").header("Authorization", "Bearer " + lr.getAccessToken()).exchange().expectStatus().isOk();
    }

   

}
