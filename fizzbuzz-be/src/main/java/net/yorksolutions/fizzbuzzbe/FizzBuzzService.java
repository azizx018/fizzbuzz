package net.yorksolutions.fizzbuzzbe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

// Controllers are collections of functions that respond to client requests
// i.e. http://duckduckgo.com?query=how_to_code_java
@Service
public class FizzBuzzService {

    private final RestTemplate rest;

    // This is for Spring
    @Autowired
    public FizzBuzzService() {
        rest = new RestTemplate();
    }

    // This is for Mockito
    public FizzBuzzService(RestTemplate rest) {
        this.rest = rest;
    }

    public void checkAuthorized(UUID token) {
        String url = "http://localhost:8081/isAuthorized?token=" + token;
        final ResponseEntity<Void> response = rest.getForEntity(url, Void.class);

        switch (response.getStatusCode()) {
            case OK:
                return;

            case UNAUTHORIZED:
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

            default:
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    String fizzbuzz(UUID token, Integer input) {
        checkAuthorized(token);
        return FizzBuzz.play(input);
    }


    public IP ip(UUID token, HttpServletRequest request) {
        checkAuthorized(token);

        return new IP(request.getRemoteAddr());
    }

}
