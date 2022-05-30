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

    @Autowired
    FizzBuzzService service;


    @GetMapping("/fizzbuzz")
    String fizzbuzz(@RequestParam UUID token, @RequestParam Integer input) {
        return service.fizzbuzz(token, input);
    }


    @GetMapping("/ip")
    public IP ip(@RequestParam UUID token, HttpServletRequest request) {
        return service.ip(token, request);
    }

    //allows us to inject a mock serviced
    public void setService(FizzBuzzService service) {
        this.service = service;
    }

}
