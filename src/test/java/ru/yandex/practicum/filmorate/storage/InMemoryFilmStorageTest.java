package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFilmStorageTest {

    FilmStorage filmStorage;
    Film film1;
    Film film2;

    @BeforeEach
    public void init() {
        filmStorage = new InMemoryFilmStorage();

        film1 = new Film();
        film1.setName("Terminator");
        film1.setDescription("About Arnold");
        film1.setDuration(20);
        film1.setReleaseDate(LocalDate.of(2020, 10, 20));

        film2 = new Film();
        film2.setName("Terminator 2");
        film2.setDescription("About Arnold and Droids");
        film2.setDuration(90);
        film2.setReleaseDate(LocalDate.of(2015, 10, 20));
    }

    @Test
    void generateId() {
        filmStorage.save(film1);

        assertEquals(1, film1.getId(), "Не верный id фильма");

        filmStorage.save(film2);
        assertEquals(2, film2.getId(), "Не верный id фильма");
    }

    @Test
    void save() {
        filmStorage.save(film1);
        assertEquals(1, film1.getId(), "Не верный id фильма");
        assertEquals(1, filmStorage.getFilms().size(),
                "Неверное количество фильмов в репозитории");

        filmStorage.save(film2);
        assertEquals(2, film2.getId(), "Не верный id фильма");
        assertEquals(2, filmStorage.getFilms().size(),
                "Неверное количество фильмов в репозитории");

        filmStorage.save(film1);
        assertEquals(1, film1.getId(), "Не верный id фильма");
        assertEquals(2, filmStorage.getFilms().size(),
                "Неверное количество фильмов в репозитории");
    }

    @Test
    void update() {
        filmStorage.save(film1);
        assertEquals(1, film1.getId(), "Не верный id фильма");
        assertEquals(1, filmStorage.getFilms().size(),
                "Неверное количество фильмов в репозитории");

        assertThrows(ModelNotFoundException.class, () -> filmStorage.update(film2));

        film1.setName("Robocop");
        filmStorage.update(film1);
        assertEquals(1, film1.getId(), "Не верный id фильма");
        assertEquals(1, filmStorage.getFilms().size(),
                "Неверное количество фильмов в репозитории");
        List<Film> films = new ArrayList<>(filmStorage.getFilms());
        assertEquals(film1, films.get(0),
                "Не совпадают обновленные фильмы в репозитории");
        assertEquals("Robocop", films.get(0).getName(),
                "Не совпадают названия фильмов после обновления");
    }

    @Test
    void getFilms() {
        filmStorage.save(film1);
        assertEquals(1, film1.getId(), "Не верный id фильма");
        assertNotNull(filmStorage.getFilms(), "Фильмы не найдены");
        assertEquals(1, filmStorage.getFilms().size(),
                "Неверное количество фильмов в репозитории");

        filmStorage.save(film2);
        assertEquals(2, film2.getId(), "Не верный id фильма");
        assertEquals(2, filmStorage.getFilms().size(),
                "Неверное количество фильмов в репозитории");
    }
}