package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.impl.GenreDbStorage;

import java.util.*;

@Service
@Slf4j
public class FilmService {

    UserStorage userStorage;
    FilmStorage filmStorage;
    UserService userService;
    GenreDbStorage genreStorage;

    @Autowired
    public FilmService(@Qualifier("db") FilmStorage filmStorage, @Qualifier("db") UserStorage userStorage,
                       UserService userService, GenreDbStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.userService = userService;
        this.genreStorage = genreStorage;
    }


    public FilmService(FilmStorage filmStorage, UserStorage userStorage,
                       UserService userService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.userService = userService;
    }

    public void addLike(int filmId, int userId) {
        User user = userService.findUserById(userId);
        if (!userStorage.getUsers().contains(user) || filmStorage.getFilm(filmId).isEmpty()) {
            log.warn("User'{}' did not like film with id {}", userId, filmId);
            throw new ModelNotFoundException(String.format("User'{}' did not like film with id {}", userId, filmId));
        }
        log.info("User'{}' likes film with id {}", userId, filmId);
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        User user = userService.findUserById(userId);
        if (!userStorage.getUsers().contains(user) || filmStorage.getFilm(filmId).isEmpty()) {
            log.warn("User'{}' did not like film with id {}", userId, filmId);
            throw new ModelNotFoundException(String.format("User'{}' did not like film with id {}", userId, filmId));
        }
        log.info("User'{}' likes film with id {}", userId, filmId);
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getTop10PopularFilms(int count) {
        List<Film> films = filmStorage.getTop10PopularFilms(count);
        genreStorage.linkGenres(films);
        return films;
    }

    public Film findFilmById(Integer id) {
        Film film = filmStorage.getFilm(id).orElseThrow(() -> {
            log.warn("Фильм с идентификатором {} не найден.", id);
            throw new ModelNotFoundException(String.format("Film № %d не найден", id));
        });
        genreStorage.linkGenres(Collections.singletonList(film));
        log.info("Got film (id '{}')", id);
        return film;
    }

    public List<Film> getFilms() {
        List<Film> films = filmStorage.getFilms();
        genreStorage.linkGenres(films);
        return films;
    }

    public void deleteLikesFromUser(int userId) {
        for (Film film : filmStorage.getFilms()) {
            if (film.getLikes().contains(userId)) {
                log.info("Removed film (id '{}') like from user with id'{}'", film.getId(),userId);
                deleteLike(film.getId(), userId);
            }
        }
    }
}
