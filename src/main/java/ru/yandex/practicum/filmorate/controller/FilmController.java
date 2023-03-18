package ru.yandex.practicum.filmorate.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.AppError;
import ru.yandex.practicum.filmorate.validator.FilmValidator;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Set<Film> films = new HashSet<>();
    private final FilmValidator filmValidator = new FilmValidator();

    @GetMapping
    public Set<Film> findAll(HttpServletRequest request) {
        log.info("Get request to endpoint: '{} {}'",
                request.getMethod(), request.getRequestURI());
        return films;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Film film) {
        try {
            if (filmValidator.validate(film)) {
                films.add(film);
                log.info("Method: POST; created film with ID = " + film.getId());
            }
            return new ResponseEntity<>(film, HttpStatus.OK);
        } catch (ValidationException ex) {
            log.warn(ex.getMessage());
            return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(),
                    "Film with id " + film.getId() + " is not created"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody Film film) {
        try {
            if (filmValidator.validate(film) && !films.isEmpty()) {
                for (Film f : films) {
                    if (f.getId() == film.getId()) {
                        films.remove(f);
                        films.add(film);
                        log.info("Method: PUT; updated film with ID = " + film.getId());
                        return new ResponseEntity<>(film, HttpStatus.OK);
                    }
                }
            }
            log.warn("Method: PUT; film with ID " + film.getId() + " is not created");
            return new ResponseEntity<>(film, HttpStatus.NOT_FOUND);
        } catch (ValidationException ex) {
            log.warn(ex.getMessage());
            return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(),
                    "Film with id " + film.getId() + " is not created"),
                    HttpStatus.NOT_FOUND);
        }
    }

}
