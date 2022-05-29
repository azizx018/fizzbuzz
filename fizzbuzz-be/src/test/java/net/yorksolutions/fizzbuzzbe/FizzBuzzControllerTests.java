package net.yorksolutions.fizzbuzzbe;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
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
public class FizzBuzzControllerTests {

    @InjectMocks
    @Spy
    FizzBuzzController controller;

    @Mock
    HttpServletRequest request;

    @Mock
    RestTemplate rest;

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
    void itShouldThrowUnauthWhenOtherStatusIsUnauth() {
        final UUID token = UUID.randomUUID();
        String url = "http://localhost:8081/isAuthorized?token=" + token;

//        {
//            RestTemplate rest;
//            rest = new RestTemplate();
//            ResponseEntity<Void> response = rest.getForEntity(url, Void.class, (Object) null);
//            response.getStatusCode(); // do something with that status code
//        }

        when(rest.getForEntity(url, Void.class))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED));
        final ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> controller.checkAuthorized(token));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    void itShouldThrowIntErrWhenOtherStatusIsOther() {
        final UUID token = UUID.randomUUID();
        String url = "http://localhost:8081/isAuthorized?token=" + token;
        when(rest.getForEntity(url, Void.class))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.CONFLICT));
        final ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> controller.checkAuthorized(token));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
    }

    @Test
    void itShouldNotThrowWhenOtherStatusIsOK() {
        final UUID token = UUID.randomUUID();
        String url = "http://localhost:8081/isAuthorized?token=" + token;
        when(rest.getForEntity(url, Void.class))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
        assertDoesNotThrow(() -> controller.checkAuthorized(token));
    }

    @Test
    void itShouldReturnTheClientsIP() {
        final String ip = "1.2.3.4";
        final IP expected = new IP(ip);
        final var token = UUID.randomUUID();
        when(request.getRemoteAddr()).thenReturn(ip);
        doNothing().when(controller).checkAuthorized(any());
        assertEquals(expected, controller.ip(token, request));
    }

    @Test
    void itShouldCallPlay() {
        final String expected = "some string";
        final var token = UUID.randomUUID();
//        final var controller = spy(new FizzBuzzController(repository, tokenMap, rest));

        doNothing().when(controller).checkAuthorized(any());
        try (final var mocked = Mockito.mockStatic(FizzBuzz.class)) { // Prepare to change source code
            mocked.when(() -> FizzBuzz.play(99)).thenReturn(expected); // change source code
//            when(tokenMap.containsKey(token)).thenReturn(true);
            assertEquals(expected, controller.fizzbuzz(token, 99));
        }
    }
}
