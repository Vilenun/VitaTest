package com.trial.VitaTest.Controller;

import com.trial.VitaTest.Logic.Request;
import com.trial.VitaTest.Logic.RequestService;
import com.trial.VitaTest.Repo.RequestUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

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
}
