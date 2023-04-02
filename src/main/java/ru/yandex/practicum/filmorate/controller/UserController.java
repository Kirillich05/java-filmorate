package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserStorage repository;
    private final UserValidator userValidator;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage repository, UserValidator userValidator,
                          UserService userService) {
        this.repository = repository;
        this.userValidator = userValidator;
        this.userService = userService;
    }

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

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User findUser(@PathVariable int userId) {
        log.info("Get user by id: '{}'", userId);
        return userService.findUserById(userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public String addFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public User deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Get common friends of user id '{}' and user '{}'", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getFriends(@PathVariable int id) {
        log.info("Get friends of user id '{}'", id);
        return userService.getFriends(id);
    }
}
