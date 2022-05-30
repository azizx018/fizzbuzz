package net.yorksolutions.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

// Controllers are collections of functions that respond to client requests
// i.e. http://duckduckgo.com?query=how_to_code_java
@RestController
@RequestMapping("/")
public class UserController {

    private UserService service;



    // This is for Spring
    @Autowired
    public UserController(@NonNull UserService service) {
        this.service = service;

    }

    @GetMapping("/login")
    UUID login(@RequestParam String username, @RequestParam String password) {
        return service.login(username, password);
    }

    @GetMapping("/register")
    public void register(@RequestParam String username, @RequestParam String password) {
        service.register(username, password);
    }

    @GetMapping("/isAuthorized")
    public void isAuthorized(@RequestParam UUID token) {
        service.isAuthorized(token);
    }


    public void setService(UserService service) {
        this.service = service;
    }

}
