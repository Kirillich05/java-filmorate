package ru.yandex.practicum.filmorate.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.AppError;
import ru.yandex.practicum.filmorate.validator.UserValidator;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Set<User> users = new HashSet<>();
    private final UserValidator userValidator = new UserValidator();

    @GetMapping
    public Set<User> findAll(HttpServletRequest request) {
        log.info("Get request to endpoint: '{} {}'",
                request.getMethod(), request.getRequestURI());
        return users;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody User user) {
        try {
            if (userValidator.validate(user)) {
                users.add(user);
                log.info("Method: POST; created user with ID = " + user.getId());
            }
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (ValidationException ex) {
            log.warn(ex.getMessage());
            return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(),
                    "User with id " + user.getId() + " is not created"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody User user) {
        try {
            if (userValidator.validate(user) && !users.isEmpty()) {
                for (User u : users) {
                    if (u.getId() == user.getId()) {
                        users.remove(u);
                        users.add(user);
                        log.info("Method: PUT; updated user with ID = " + user.getId());
                        return new ResponseEntity<>(user, HttpStatus.OK);
                    }
                }
            }
                log.warn("Method: PUT; user with ID " + user.getId() + " is not created");
                return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
        } catch (ValidationException ex) {
            log.warn(ex.getMessage());
            return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(),
                    "User with id " + user.getId() + " is not created"),
                    HttpStatus.NOT_FOUND);
        }
    }
}
