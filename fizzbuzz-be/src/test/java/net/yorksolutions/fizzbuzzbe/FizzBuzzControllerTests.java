package net.yorksolutions.fizzbuzzbe;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

// Mockito - is a framework that makes it easy to mock
//    java classes
// jest.fn()

// jsontest.com - out of business
@ExtendWith(MockitoExtension.class)
@SpringBootTest (webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FizzBuzzControllerTests {
    @LocalServerPort
    int port;

    @Autowired
    FizzBuzzController controller;

    @Mock
    FizzBuzzService service;

    @BeforeEach
    void setup(){
        controller.setService(service);
    }

    // This is the wrong way to test boolean logic
//    @Test
//    void itShouldReturnUnathWhenUsernameAndPasswordIsWrong() {
//        lenient().when(repository.findByUsernameAndPassword("bad", "bad")).thenReturn(Optional.empty());
//        assertThrows(ResponseStatusException.class, () -> controller.login("bad", "bad"));
//    }

    // The fizz controller will ask another controller if this user is authorized
    // URL of the other controller
    // Pass the token that the user is providing
    // Possible reponses:
    // 1. OK - the user is authorized, we can proceed
    // 2. UNAUTHORIZED - we can throw UNAUTH
    // 3. Some other status code - throw INTERNAL_SERVER_ERROR


    @Test
    void itShouldCallIPWithTheTokenAndTheHttpRequestAndReturnIP() {
        final TestRestTemplate rest = new TestRestTemplate();
        final String ip = "1.2.3.4";
        final IP expected = new IP(ip);
        final var token = UUID.randomUUID();
        String url = "http://localhost:" + port + "/ip?token=" + token;
        final ArgumentCaptor<HttpServletRequest> captor = ArgumentCaptor.forClass(HttpServletRequest.class);
        //this captures and we can investigate the values
        when(service.ip(eq(token), captor.capture())).thenReturn(expected);
        final ResponseEntity<IP> response = rest.getForEntity(url, IP.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
    }
    @Test
    void itShouldCallFizzBuzzWithTheTokenAndTheInput() {
        final TestRestTemplate rest = new TestRestTemplate();
        final var token = UUID.randomUUID();
        final int input = 3;
        final String output = "some output";
        String url = "http://localhost:" + port + "/fizzbuzz?token=" + token + "&input=" + input;
        when(service.fizzbuzz(token, input)).thenReturn(output);
        final ResponseEntity<String> response = rest.getForEntity(url, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(output, response.getBody());
    }
//
//    @Test
//    void itShouldCallPlay() {
//        final String expected = "some string";
//        final var token = UUID.randomUUID();
////        final var controller = spy(new FizzBuzzController(repository, tokenMap, rest));
//
//        doNothing().when(controller).checkAuthorized(any());
//        try (final var mocked = Mockito.mockStatic(FizzBuzz.class)) { // Prepare to change source code
//            mocked.when(() -> FizzBuzz.play(99)).thenReturn(expected); // change source code
////            when(tokenMap.containsKey(token)).thenReturn(true);
//            assertEquals(expected, controller.fizzbuzz(token, 99));
//        }
//    }
}
