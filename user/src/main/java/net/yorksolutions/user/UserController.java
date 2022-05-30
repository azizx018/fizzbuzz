package net.yorksolutions.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.util.HashMap;
import java.util.UUID;

// Controllers are collections of functions that respond to client requests
// i.e. http://duckduckgo.com?query=how_to_code_java
@RestController
@RequestMapping("/")
public class UserController {


    private UserAccountRepository repository;

    private HashMap<UUID, Long> tokenMap;

    // This is for Spring
    @Autowired
    public UserController(@NonNull UserAccountRepository repository) {
        this.repository = repository;
        tokenMap = new HashMap<>();

    }

    // This is for Mockito
    public UserController(@NonNull UserAccountRepository repository,
                          @NonNull HashMap<UUID, Long> tokenMap) {
        this.repository = repository;
        this.tokenMap = tokenMap;

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
    @GetMapping("/isAuthorized")
    public void isAuthorized(@RequestParam UUID token) {
        if (tokenMap.containsKey(token))
            return;

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    public void setTokenMap(HashMap<UUID, Long> tokenMap) {
        this.tokenMap = tokenMap;
    }
    public void setRepository(UserAccountRepository repository) {
        this.repository = repository;
    }
}
