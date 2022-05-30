package net.yorksolutions.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.Id;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@SpringBootTest (webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)

class UserApplicationTests {
    @LocalServerPort
    int port;

    @Autowired
    UserController controller;

    @Mock
    HashMap<UUID, Long> tokenMap;

    @Mock
    UserAccountRepository repository;

    @BeforeEach
    void setup() {
        controller.setTokenMap(tokenMap);
        controller.setRepository(repository);
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

    @Test
    void itShouldRespondUnauthWhenUsernameBad() {
        final TestRestTemplate rest = new TestRestTemplate();
        final String username = "some username";
        final String password = "some password";
        String url = "http://localhost:" + port + "/login?username=" + username + "&password=" + password;
        lenient().when(repository.findByUsernameAndPassword(not(eq(username)), eq(password))).thenReturn(Optional.of(new UserAccount()));
        when(repository.findByUsernameAndPassword(username, password)).thenReturn(Optional.empty());
        final ResponseEntity<Void> response = rest.getForEntity(url, Void.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

    }
    @Test
    void itShouldRespondUnauthWhenPasswordBad() {
        final TestRestTemplate rest = new TestRestTemplate();
        final String username = "some username";
        final String password = "some password";
        String url = "http://localhost:" + port + "/login?username=" + username + "&password=" + password;
        lenient().when(repository.findByUsernameAndPassword(eq(username), not(eq(password)))).thenReturn(Optional.of(new UserAccount()));
        when(repository.findByUsernameAndPassword(username, password)).thenReturn(Optional.empty());
        final ResponseEntity<Void> response = rest.getForEntity(url, Void.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

    }
    @Test
    void itShouldRespondWithTokenWhenLoginValid() {
        final TestRestTemplate rest = new TestRestTemplate();
        final String username = "some username";
        final String password = "some password";
        String url = "http://localhost:" + port + "/login?username=" + username + "&password=" + password;
        when(repository.findByUsernameAndPassword(username, password)).thenReturn(Optional.of(new UserAccount()));
        final ResponseEntity<UUID> response = rest.getForEntity(url, UUID.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UUID token = response.getBody();
        assertNotNull(token);

    }

}
