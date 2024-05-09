package com.trial.VitaTest.Controller;

import com.trial.VitaTest.Logic.Request;
import com.trial.VitaTest.Logic.RequestService;
import com.trial.VitaTest.Logic.RequestStatus;
import com.trial.VitaTest.Logic.RequestUser;
import com.trial.VitaTest.Repo.RequestUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@RestController
public class RequestController {
    @Autowired
    RequestService requestService;
    private final RequestUserRepository repository;

    private final PasswordEncoder passwordEncoder;
    public RequestController(RequestUserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(path = "/register")
    public ResponseEntity register(@RequestBody RegistrationRequest register) {
        var user = new RequestUser();
        if (repository.findRequestUserByUsername(register.username()).isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        user.setUsername(register.username());
        user.setPassword(passwordEncoder.encode(register.password()));
        user.setAuthority(register.authority());

        repository.save(user);

        return ResponseEntity.ok().body(user.getUsername());
    }
    @PostMapping(path = "/create")
    public ResponseEntity create(@RequestBody RequestRequest request, Principal principal){
        var query = new Request();
        query.setName(request.request);
        query.setUser(repository.findRequestUserByUsername(principal.getName()).get());
        query.setRequestStatus(RequestStatus.DRAFT.getStatus());
        requestService.save(query);
        return ResponseEntity.ok("done");
    }
    @GetMapping(path = "/test")
    public String test() {
        return "Access to '/test' granted";
    }
    @GetMapping(path = "/check/asc",params = "page")
    public ResponseEntity checkAsc(Principal principal,@RequestParam(defaultValue = "1") int page) {
        if (page < 1){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Page<Request> chosenPage = requestService.findIdAsc(repository.findRequestUserByUsername(principal.getName()).get().getId(), PageRequest.of(page - 1, 5));
        if (chosenPage.getTotalPages() < page){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(chosenPage.getContent());
    }
    @GetMapping(path = "/check/desc",params = "page")
    public ResponseEntity checkDesc(Principal principal,@RequestParam(defaultValue = "1") int page) {
        if (page < 1){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Page<Request> chosenPage = requestService.findIdDesc(repository.findRequestUserByUsername(principal.getName()).get().getId(), PageRequest.of(page - 1, 5));
        if (chosenPage.getTotalPages() < page){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(chosenPage.getContent());
    }
    @PutMapping(path = "/update", params = "id")
    public ResponseEntity updateRequest(Principal principal, @RequestParam Long id, @RequestBody RequestRequest request){
        if (requestService.findId(id).isEmpty()){
            return ResponseEntity.badRequest().body("No request found");
        }
        if (repository.findRequestUserByUsername(principal.getName()).get().getId() != requestService.findId(id).get().getUser().getId())
        {
            return ResponseEntity.badRequest().body("No request found");
        }
        if (!RequestStatus.DRAFT.getStatus().equals(requestService.findId(id).get().getRequestStatus())){
            return ResponseEntity.badRequest().body("Request is not for draft, can't change");
        }
        try{
            requestService.update(id, request.request);
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping(path = "/send", params = "id")
    public ResponseEntity send (Principal principal, @RequestParam Long id){
        if (requestService.findId(id).isEmpty()){
            return ResponseEntity.badRequest().body("No request found");
        }
        if (repository.findRequestUserByUsername(principal.getName()).get().getId() != requestService.findId(id).get().getUser().getId())
        {
            return ResponseEntity.badRequest().body("No request found");
        }
        if (!RequestStatus.DRAFT.getStatus().equals(requestService.findId(id).get().getRequestStatus())){
            return ResponseEntity.badRequest().body("Request is already sent, can't change");
        }
        try{
            requestService.send(id);
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @GetMapping(path = "/checkSent/asc",params = "page")
    public ResponseEntity checkSentAsc(Principal principal,@RequestParam(defaultValue = "1") int page) {
        if (page < 1){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Page<Request> chosenPage = requestService.findIdAsc(repository.findRequestUserByUsername(principal.getName()).get().getId(), PageRequest.of(page - 1, 5));
        if (chosenPage.getTotalPages() < page){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(chosenPage.getContent());
    }

    record RegistrationRequest(String username, String password, String authority) { }
    record RequestRequest(String request) { }


}
