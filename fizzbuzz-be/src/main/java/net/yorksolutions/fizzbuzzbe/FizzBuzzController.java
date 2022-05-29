package net.yorksolutions.fizzbuzzbe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.UUID;

// Controllers are collections of functions that respond to client requests
// i.e. http://duckduckgo.com?query=how_to_code_java
@RestController
@RequestMapping("/")
public class FizzBuzzController {
    private final UserAccountRepository repository;
    private final HashMap<UUID, Long> tokenMap;

    private final RestTemplate rest;

    // This is for Spring
    @Autowired
    public FizzBuzzController(@NonNull UserAccountRepository repository) {
        this.repository = repository;
        tokenMap = new HashMap<>();
        rest = new RestTemplate();
    }

    // This is for Mockito
    public FizzBuzzController(@NonNull UserAccountRepository repository,
                              @NonNull HashMap<UUID, Long> tokenMap, RestTemplate rest) {
        this.repository = repository;
        this.tokenMap = tokenMap;
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

    @GetMapping("/")
    String echo(@RequestParam String input) {
        return input;
    }

    @GetMapping("/fizzbuzz")
    String fizzbuzz(UUID token, @RequestParam Integer input) {
        checkAuthorized(token);
        return FizzBuzz.play(input);
    }

    // What we want:
    // {
    //     "ip": "1.2.3.4"
    // }

    public IP ip(UUID token, HttpServletRequest request) {
        if (!tokenMap.containsKey(token))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return new IP(request.getRemoteAddr());
    }

    @GetMapping("/login")
    UUID login(@RequestParam String username, @RequestParam String password) {
        // check to see if the username exists
        // Search for a UserAccount with the given username and password
        var result = repository.findByUsernameAndPassword(username, password);

        // If not found, inform the client that they are unauthorized
        if (result.isEmpty())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        // If found:
        // Generate a token that is to be used for all future requests that are associated
        //     w/ this user account
        final var token = UUID.randomUUID();
        // Associate the generated token w/ the user account
        tokenMap.put(token, result.get().id);
        // Provide the generated token to the client for future use
        return token; // from now on, use this uuid to let me know who you are
    }

    @GetMapping("/register")
    public void register(@RequestParam String username, @RequestParam String password) {
        if (repository.findByUsername(username).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        repository.save(new UserAccount());
    }
}
