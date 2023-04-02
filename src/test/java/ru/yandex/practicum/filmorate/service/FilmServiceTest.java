package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {

    private UserStorage userStorage;
    private FilmStorage filmStorage;
    private FilmService filmService;
    private Film film;

    @BeforeEach
    public void init() {
        userStorage = new InMemoryUserStorage();
        filmStorage = new InMemoryFilmStorage();
        UserService userService = new UserService(userStorage);
        filmService = new FilmService(filmStorage, userStorage, userService);
        film = new Film();
        film.setName("Terminator");
        film.setDescription("About Arnold");
        film.setDuration(20);
        film.setReleaseDate(LocalDate.of(2020, 10, 20));
        filmStorage.save(film);

        User user = new User();
        user.setName("Karl");
        user.setEmail("karnel@yandex.ru");
        user.setLogin("karnel");
        user.setBirthday(LocalDate.of(2000, 10, 2));
        userStorage.save(user);
    }

    @Test
    void addLike() {
        assertEquals(1, filmStorage.getFilms().size(), "Неверное количество фильмов");
        assertEquals(1, film.getId(), "Неверный id фильма");
        assertThrows(ModelNotFoundException.class, () -> filmService.findFilmById(2));
        assertNotNull(filmService.findFilmById(1), "Фильм не найден");
        assertEquals(film, filmService.findFilmById(1), "Фильмы не совпадают");

        assertThrows(ModelNotFoundException.class, () -> filmService.addLike(2, 1));
        filmService.addLike(1,1);
        filmService.addLike(1,1);
        assertEquals(1, film.getAmountFilmLikes(), "Неверное количество лайков");

        User user2 = new User();
        user2.setName("Karl");
        user2.setEmail("karnel@yandex.ru");
        user2.setLogin("karnel");
        user2.setBirthday(LocalDate.of(2000, 10, 2));
        userStorage.save(user2);
        filmService.addLike(1,2);
        assertEquals(2, film.getAmountFilmLikes(), "Неверное количество лайков");
    }

    @Test
    void deleteLike() {
        assertEquals(1, filmStorage.getFilms().size(), "Неверное количество фильмов");
        assertEquals(1, film.getId(), "Неверный id фильма");
        assertThrows(ModelNotFoundException.class, () -> filmService.findFilmById(2));
        assertNotNull(filmService.findFilmById(1), "Фильм не найден");
        assertEquals(film, filmService.findFilmById(1), "Фильмы не совпадают");

        assertThrows(ModelNotFoundException.class, () -> filmService.deleteLike(2, 1));
        filmService.deleteLike(1,1);
        assertEquals(0, film.getAmountFilmLikes(), "Неверное количество лайков");
    }

    @Test
    void getTop10PopularFilms() {
        Film film2 = new Film();
        film2.setName("Terminator 2");
        film2.setDescription("About Arnold and Droids");
        film2.setDuration(90);
        film2.setReleaseDate(LocalDate.of(2015, 10, 20));
        filmStorage.save(film2);
        assertEquals(2, filmStorage.getFilms().size(), "Неверное количество фильмов");
        filmService.addLike(2,1);
        assertEquals(1, film2.getAmountFilmLikes(), "Неверное количество лайков");
        assertEquals(0, film.getAmountFilmLikes(), "Неверное количество лайков");

        List<Film> topFilm = filmService.getTop10PopularFilms(2);
        assertEquals(2, topFilm.size(), "Неверный размер топ фильмов");
        assertEquals(film2, topFilm.get(0), "Неправильный топ фильмов. Фильмы не совпадают");
        assertEquals(film, topFilm.get(1), "Неправильный топ фильмов. Фильмы не совпадают");
    }

    @Test
    void findFilmById() {
        assertEquals(1, filmStorage.getFilms().size(), "Неверное количество фильмов");
        assertEquals(1, film.getId(), "Неверный id фильма");
        assertThrows(ModelNotFoundException.class, () -> filmService.findFilmById(2));

        assertNotNull(filmService.findFilmById(1), "Фильм не найден");
        assertEquals(film, filmService.findFilmById(1), "Фильмы не совпадают");

        Film film2 = new Film();
        film2.setName("Terminator 2");
        film2.setDescription("About Arnold and Droids");
        film2.setDuration(90);
        film2.setReleaseDate(LocalDate.of(2015, 10, 20));
        filmStorage.save(film2);
        assertEquals(2, filmStorage.getFilms().size(), "Неверное количество фильмов");
        assertNotNull(filmService.findFilmById(2), "Фильм не найден");
        assertEquals(film2, filmService.findFilmById(2), "Фильмы не совпадают");
    }
}