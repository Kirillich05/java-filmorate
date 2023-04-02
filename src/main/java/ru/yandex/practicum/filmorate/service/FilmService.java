package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    UserStorage userStorage;
    FilmStorage filmStorage;
    UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.userService = userService;
    }


    public String addLike(int filmId, int userId) {
        Film film = findFilmById(filmId);
        User user = userService.findUserById(userId);

        if (userStorage.getUsers().contains(user) &&
                filmStorage.getFilms().contains(film)) {
            film.getLikes().add(user.getId());
            log.info("User'{}' likes film with id {}", userId, filmId);
            return "Added like";
        } else {
            log.warn("User'{}' did not like film with id {}", userId, filmId);
            throw new ModelNotFoundException(String.format("User'{}' did not like film with id {}", userId, filmId));
        }
    }

    public Film deleteLike(int filmId, int userId) {
        Film film = findFilmById(filmId);
        User user = userService.findUserById(userId);

        if (userStorage.getUsers().contains(user) &&
                filmStorage.getFilms().contains(film)) {
            film.getLikes().remove(user.getId());
            log.info("Removed film like {} from user'{}'", filmId, userId);
            return film;
        } else {
            log.warn("Did not remove film like {} from user'{}'", filmId, userId);
            throw new ModelNotFoundException(String.format("Did not remove film like {} from user'{}'", filmId, userId));
        }
    }

    public List<Film> getTop10PopularFilms(int count) {
        List<Film> allFilms = new ArrayList<>(filmStorage.getFilms());
        List<Film> top = allFilms.stream()
                .sorted(Comparator.comparing(Film::getAmountFilmLikes, Comparator.reverseOrder()))
                .limit(count)
                .collect(Collectors.toList());
        return top;
    }

    public Film findFilmById(Integer id) {
        return filmStorage.getFilms().stream()
                .filter(x -> x.getId() == id)
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("There is not film by id: '{}'", id);
                    return new ModelNotFoundException(String.format("Film № %d не найден", id));
                });
    }
}
