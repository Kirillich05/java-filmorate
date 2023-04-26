package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@Slf4j
public class GenreService {

    GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre getGenre(int id) {
        log.info("Genre with id {} received", id);
        return genreStorage.getGenres().stream()
                .filter(x -> x.getId() == id)
                .findFirst()
                .orElseThrow(() -> {
            log.warn("Genre with id {} is not found", id);
            return new ModelNotFoundException("Genre with id {} is not found" + id);
        });
    }

    public List<Genre> getAllGenre() {
        log.info("All genres received");
        return genreStorage.getGenres();
    }
}
