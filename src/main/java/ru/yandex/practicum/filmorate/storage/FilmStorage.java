package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    void save(Film film);

    void update(Film film);

    boolean delete(int id);

    List<Film> getFilms();

    Optional<Film> getFilm(int id);

    List<Film> getTop10PopularFilms(int count);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);
}
