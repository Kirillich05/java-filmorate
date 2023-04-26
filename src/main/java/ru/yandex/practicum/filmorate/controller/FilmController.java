package ru.yandex.practicum.filmorate.controller;

import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.AccessLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmController {

    FilmStorage repository;
    FilmValidator filmValidator;
    FilmService filmService;

    @Autowired
    public FilmController(@Qualifier("db") FilmStorage repository, FilmValidator filmValidator,
                          FilmService filmService) {
        this.repository = repository;
        this.filmValidator = filmValidator;
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> findAll(HttpServletRequest request) {
        log.info("Get request to endpoint: '{} {}'",
                request.getMethod(), request.getRequestURI());
        return filmService.getFilms();
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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFilm(@PathVariable int id) {
        log.info("Method: DELETE; delete film with ID = " + id);
        repository.delete(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Film findFilm(@PathVariable int id) {
        log.info("Get film by id: '{}'", id);
        return filmService.findFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public String addUsersLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Film with id: '{}' got user like with id '{}'", id, userId);
        filmService.addLike(id, userId);
        return "Add like!";
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Film with id: '{}' lost user like with id '{}'", id, userId);
        filmService.deleteLike(id, userId);
        return "Delete like!";
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getTopFilms(@RequestParam(required = false, defaultValue = "10") String count) {
        log.info("Get top films with count: '{}'", count);
        return filmService.getTop10PopularFilms(Integer.parseInt(count));
    }
}
