package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int generatorId = 0;
    private final Map<Integer, Film> films = new HashMap<>();

    private int generateId() {
        return ++generatorId;
    }

    @Override
    public void save(Film film) {
        if (film.getId() != 0 && films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            int id = generateId();
            film.setId(id);
            films.put(id, film);
        }
    }

    @Override
    public void update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            throw new ModelNotFoundException("Don't update because there isn't " + film.getId() +
                    " in the repository");
        }
    }

    @Override
    public boolean delete(int id) {
        if (films.containsKey(id)) {
            films.remove(id);
            return true;
        } else {
            throw new ModelNotFoundException("Don't delete film because there isn't " + id +
                    " in the repository");
        }
    }

    @Override
    public List<Film> getFilms() {
        List<Film> allFilms = new ArrayList<>();
        for (Film film : films.values()) {
            allFilms.add(film);
        }
        return allFilms;
    }

    @Override
    public Optional<Film> getFilm(int id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public List<Film> getTop10PopularFilms(int count) {
        return null;
    }

    @Override
    public void addLike(int filmId, int userId) {
        Film film = films.get(filmId);
        film.getLikes().add(userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        Film film = films.get(filmId);
        film.getLikes().remove(userId);
    }
}
