package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.UserRepository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collection;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserRepository repository = new UserRepository();
    private final UserValidator userValidator = new UserValidator();

    @GetMapping
    public Collection<User> findAll(HttpServletRequest request) {
        log.info("Get request to endpoint: '{} {}'",
                request.getMethod(), request.getRequestURI());
        return repository.getUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        if (userValidator.validate(user)) {
            repository.save(user);
            log.info("Method: POST; created user with ID = " + user.getId());
        }
        return user;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public User update(@Valid @RequestBody User user) {
        if (userValidator.validate(user)) {
            repository.update(user);
            log.info("Method: PUT; updated user with ID = " + user.getId());
        }
        return user;
    }
}
