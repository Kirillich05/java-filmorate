package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.FilmRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@Validated
@Slf4j
public class FilmController {

    private final FilmRepository repository = new FilmRepository();
    private final FilmValidator filmValidator = new FilmValidator();

    @GetMapping
    public Collection<Film> findAll(HttpServletRequest request) {
        log.info("Get request to endpoint: '{} {}'",
                request.getMethod(), request.getRequestURI());
        return repository.getFilms();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        if (filmValidator.validate(film)) {
            repository.save(film);
            log.info("Method: POST; created film with ID = " + film.getId());
        }
        return film;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film update(@Valid @RequestBody Film film) {
        if (filmValidator.validate(film)) {
            repository.update(film);
            log.info("Method: PUT; updated film with ID = " + film.getId());
        }
        return film;
    }
}
