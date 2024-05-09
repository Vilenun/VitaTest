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

// Созданная для тестирования в первую очередь, система регистрации. Программа вполне может работать и без неё

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
// Создание заявки. Сразу присваивается статус "Черновик". Доступ есть ТОЛЬКО у авторизованных пользователей

    @PostMapping(path = "/create")
    public ResponseEntity create(@RequestBody RequestRequest request, Principal principal){
        var query = new Request();
        query.setRequest(request.request);
        query.setUser(repository.findRequestUserByUsername(principal.getName()).get());
        query.setRequestStatus(RequestStatus.DRAFT.getStatus());
        requestService.save(query);
        return ResponseEntity.ok("done");
    }

// Просмотр созданных авторизованным человеком заявок. Берёт id из авторизации, так что даже если захотеть - чужое не увидеть. Доступно авторизованному пользователю
    @GetMapping(path = "/checkRequests")
    public ResponseEntity checkAsc(Principal principal,
                                   @RequestParam(defaultValue = "1",required = false) int page, //Если страница не выбрана - будет первая
                                   @RequestParam (defaultValue = "asc", required = false) String order){
        if (page < 1){

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        }
        try {

            Page<Request> chosenPage = requestService.findRequests(
                    repository.findRequestUserByUsername(principal.getName()).get().getId(),
                    PageRequest.of(page - 1, 5),  //Пагинация от нуля, поэтому -1, так как нулевая страница воспринимается хуже, чем первая
                    order
            );
            if (chosenPage.getTotalPages() < page){
                return ResponseEntity.badRequest().body("Page doesn't exist");
            }
            return ResponseEntity.ok(chosenPage.getContent());

        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body("Can only choose \"asc\" and \"desc\"");
        }

    }

// Обновление заявки
    @PutMapping(path = "/update", params = "id")
    public ResponseEntity updateRequest(Principal principal,
                                        @RequestParam Long id,
                                        @RequestBody RequestRequest request){
//Если заявки с таким id нет, то редактировать нечего
        if (requestService.findId(id).isEmpty()){
            return ResponseEntity.badRequest().body("No request found");
        }
//Если id того, кто создал заявку не тот, что у авторизованного, то редактировать нельзя
        if (repository.findRequestUserByUsername(principal.getName()).get().getId() != requestService.findId(id).get().getUser().getId()){
            return ResponseEntity.badRequest().body("Not your request");
        }
// Если статус - не Черновик, то редактировать нельзя
        if (!RequestStatus.DRAFT.getStatus().equals(requestService.findId(id).get().getRequestStatus())){
            return ResponseEntity.badRequest().body("Request is not for draft, can't change");
        }

        try{
            requestService.update(id, request.request);
        } catch(Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

//Присваивание заявке статус "Отправлено"
    @PutMapping(path = "/send", params = "id")
    public ResponseEntity send (Principal principal, @RequestParam Long id){
//Если заявки с таким id нет, то редактировать нечего
        if (requestService.findId(id).isEmpty()){
            return ResponseEntity.badRequest().body("No request found");
        }
//Если id того, кто создал заявку не тот, что у авторизованного, то редактировать нельзя
        if (repository.findRequestUserByUsername(principal.getName()).get().getId() != requestService.findId(id).get().getUser().getId())
        {
            return ResponseEntity.badRequest().body("Not your request");
        }
// Если статус - не Черновик, то она уже отправлена. Редактировать нельзя
        if (!RequestStatus.DRAFT.getStatus().equals(requestService.findId(id).get().getRequestStatus())){
            return ResponseEntity.badRequest().body("Request is already sent, can't change");
        }

        try{
            requestService.send(id);
        } catch(Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

// Проверка всех отправленных заявок оператором
    @GetMapping(path = "/checkSent")
    public ResponseEntity checkSentAsc(Principal principal,
                                       @RequestParam(defaultValue = "1", required = false) int page,
                                       @RequestParam (defaultValue = "asc", required = false) String order) {

        if (page < 1){
            return ResponseEntity.badRequest().body("Incorrect page!");
        }
        try{
            // Принимает порядок (asc или desc) и сортирует по нужному порядку
            Page<Request> chosenPage = requestService.findSent(PageRequest.of(page - 1, 5),order);
            // Если пытаешься вызвать страницу, которой нет - не позволяет
            if (chosenPage.getTotalPages() < page){
                return ResponseEntity.badRequest().body("Incorrect page!");
            }
            return ResponseEntity.ok(chosenPage.getContent()
                    .stream().peek(e -> e.setRequest(e.getRequest().replaceAll("(.{1})(?!$)", "$1-"))
                    ).toList());
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body("Can only choose \"asc\" and \"desc\"");
        }
    }
    // Проверка отправленных заявок оператором по части имени
    @GetMapping(path = "/checkName")
    public ResponseEntity checkNameDesc(Principal principal,
                                        @RequestParam(defaultValue = "1", required = false) int page,
                                        @RequestParam String name,
                                        @RequestParam (defaultValue = "asc", required = false) String order) {

        if (page < 1){
            return ResponseEntity.badRequest().body("Incorrect page!");
        }
        //Если таких людей нет - вернёт пустую строку
        if (repository.findByUsernameIgnoreCaseContains(name).isEmpty() || repository.findByUsernameIgnoreCaseContains(name).get().stream().toList().isEmpty()){
            return ResponseEntity.ok().body("");
        }

        try {
            List<Integer> ids = repository.findByUsernameIgnoreCaseContains(name).get().stream()
                                                            .map(RequestUser::getId).toList();
            Page<Request> chosenPage = requestService.findName(ids, PageRequest.of(page - 1, 5), order);
            if (chosenPage.getTotalPages() < page) {
                return ResponseEntity.badRequest().body("Incorrect page!");
            }

            return ResponseEntity.ok(chosenPage.getContent()
            .stream().peek(e -> e.setRequest(e.getRequest().replaceAll("(.{1})(?!$)", "$1-"))
            ).toList());
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body("Can only choose \"asc\" and \"desc\"");
        }
    }

// Принятие решения оператором, как принять, так и отклонить
    @PutMapping (path = "/decision")
    public ResponseEntity decide(@RequestParam Long id,
                                 @RequestParam String decision) {
        if (requestService.findId(id).isEmpty()){
            return ResponseEntity.badRequest().body("No request found");
        }
// Не отправлено - нельзя
        if (!RequestStatus.SENT.getStatus().equals(requestService.findId(id).get().getRequestStatus())){
            return ResponseEntity.badRequest().body("Request is not for SENT, can't change");
        }

        try{
            requestService.decide(decision.toLowerCase(), id);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body("Wrong decision");
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
// Просмотр администратором всех пользователей ИЛИ, если указать имя или часть - то все подходящие
    @GetMapping (path = "/users")
    public ResponseEntity checkUsers(@RequestParam (value = "name", required=false) String name) {
        // Не указано имя - найдёт всех
        if (name == null || name.isEmpty()){
            return ResponseEntity.ok(repository.findAll());
        }

        else if (repository.findByUsernameIgnoreCaseContains(name).isEmpty() ||
                repository.findByUsernameIgnoreCaseContains(name).get().stream().toList().isEmpty()){
            return ResponseEntity.badRequest().body("No user found");
        }

        return ResponseEntity.ok().body(repository.findByUsernameIgnoreCaseContains(name).get());

    }
// Возможность администратору дать пользователю роль оператора
    @PutMapping (path = "/giveOperatorRole")
    public ResponseEntity changeAuthority(@RequestParam Long id){
        if (repository.findById(id).isEmpty()){
            return ResponseEntity.badRequest().body("No user found");
        }

        RequestUser user = repository.findById(id).get();
        if (user.getAuthority().equals("ROLE_USER")){
            user.setAuthority("ROLE_OPER");
            repository.save(user);
        } else return ResponseEntity.badRequest().body("Can't change that role");

        return ResponseEntity.ok().body("done");
    }

    record RegistrationRequest(String username, String password, String authority) { }
    record RequestRequest(String request) { }


}
