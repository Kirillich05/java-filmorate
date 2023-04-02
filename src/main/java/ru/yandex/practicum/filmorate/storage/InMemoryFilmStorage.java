package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int generatorId = 0;
    private final Map<Integer, Film> films = new HashMap<>();

    private int generateId() {
        return ++generatorId;
    }

    public void save(Film film) {
        if (film.getId() != 0 && films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            int id = generateId();
            film.setId(id);
            films.put(id, film);
        }
    }

    public void update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            throw new ModelNotFoundException("Don't update because there isn't " + film.getId() +
                    " in the repository");
        }
    }

    public Collection<Film> getFilms() {
        return films.values();
    }
}
