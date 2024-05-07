package com.trial.VitaTest.Controller;

import com.trial.VitaTest.Logic.Request;
import com.trial.VitaTest.Logic.RequestService;
import com.trial.VitaTest.Logic.RequestUser;
import com.trial.VitaTest.Repo.RequestUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class RequestController {
    @Autowired
    RequestService queriesService;
    private final RequestUserRepository repository;

    private final PasswordEncoder passwordEncoder;
    public RequestController(RequestUserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(path = "/register")
    public ResponseEntity register(@RequestBody RegistrationRequest request) {
        var user = new RequestUser();
        if (repository.findRequestUserByUsername(request.username()).isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setAuthority(request.authority());

        repository.save(user);

        return ResponseEntity.ok().body(user.getUsername());
    }

    @GetMapping(path = "/test")
    public String test() {
        return "Access to '/test' granted";
    }
    record RegistrationRequest(String username, String password, String authority) { }
}
