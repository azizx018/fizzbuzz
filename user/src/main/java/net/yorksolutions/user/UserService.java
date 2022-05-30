package net.yorksolutions.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.UUID;

@Service
public class UserService {


    private UserAccountRepository repository;

    private HashMap<UUID, Long> tokenMap;

    // This is for Spring
    @Autowired
    public UserService( UserAccountRepository repository) {
        this.repository = repository;
        tokenMap = new HashMap<>();

    }

    // This is for Mockito
    public UserService( UserAccountRepository repository,
                        HashMap<UUID, Long> tokenMap) {
        this.repository = repository;
        this.tokenMap = tokenMap;

    }

    UUID login( String username, String password) {
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


    public void register( String username,String password) {
        if (repository.findByUsername(username).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        repository.save(new UserAccount());
    }

    public void isAuthorized( UUID token) {
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
