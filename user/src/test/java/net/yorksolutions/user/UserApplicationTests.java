package net.yorksolutions.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.Id;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest (webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserApplicationTests {
    @LocalServerPort
    int port;

    @Autowired
    UserController controller;

    @Mock
    HashMap<UUID, Long> tokenMap;

    @BeforeEach
    void setup() {
        controller.setTokenMap(tokenMap);
    }

    @Test
    void contextLoads() {
    }

    //this is an integration test- not mocking anything
    @Test
    void itShouldRespondUnauthWhenTokenIsWrong() {
        TestRestTemplate rest = new TestRestTemplate();
        final UUID token = UUID.randomUUID();
        String url = "http://localhost:" + port + "/isAuthorized?token=" + token;
        final ResponseEntity<Void> response = rest.getForEntity(url, Void.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void itShouldRespondOKWhenTokenIsGood() {
        //register
        //login and get token
        //call isAuth with token
        TestRestTemplate rest = new TestRestTemplate();
        final UUID token = UUID.randomUUID();
        String url = "http://localhost:" + port + "/isAuthorized?token=" + token;
        when(tokenMap.containsKey(token)).thenReturn(true);
        final ResponseEntity<Void> response = rest.getForEntity(url, Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

}
