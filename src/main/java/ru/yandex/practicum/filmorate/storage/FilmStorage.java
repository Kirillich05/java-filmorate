package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    void save(Film film);

    void update(Film film);

    String delete(int id);

    Collection<Film> getFilms();
}
