package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {

    private final GenreDbStorage genreDbStorage;

    @Test
    void getGenres() {
        assertNotNull(genreDbStorage.getGenres());
        assertEquals(6, genreDbStorage.getGenres().size());
    }

    @Test
    void getGenre() {
        Genre genre = Genre.builder()
                .name("Боевик")
                .build();
        String nameGenre = genre.getName();
        assertEquals(nameGenre, genreDbStorage.getGenre(6).get().getName());
    }

    @Test
    void linkGenres() {
        Film film = Film.builder()
                .name("Harry Potter")
                .description("About magic world")
                .releaseDate(LocalDate.of(2001, 1, 10))
                .duration(120)
                .mpa(new Mpa(1, "G"))
                .genres(null)
                .likes(new LinkedHashSet<>())
                .build();
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        genres.add(genreDbStorage.getGenre(1).get());
        film.setGenres(genres);
        List<Film> films = new ArrayList<>();
        films.add(film);

        genreDbStorage.linkGenres(films);
        Optional<Genre> opt = genreDbStorage.getGenre(1);
        Genre comedy = opt.get();

        assertTrue(film.getGenres().contains(comedy));
    }
}