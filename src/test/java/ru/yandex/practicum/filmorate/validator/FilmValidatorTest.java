package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dao.FilmRepository;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidatorTest {

    @Test
    void checkEmptyName() {
        FilmValidator filmValidator = new FilmValidator();
        Film film = new Film();
        film.setName("");
        assertThrows(ValidationException.class, () -> filmValidator.validate(film));
    }

    @Test
    void checkMaxLengthDescription() {
        FilmValidator filmValidator = new FilmValidator();
        Film film = new Film();
        film.setName("Terminator");
        film.setDuration(20);
        film.setReleaseDate(LocalDate.of(2020, 10, 20));

        StringBuilder longDescription = new StringBuilder();
        for (int i = 0; i < 201; i++) {
            longDescription.append("i");
        }
        film.setDescription(longDescription.toString());
        assertThrows(ValidationException.class, () -> filmValidator.validate(film));
    }

    @Test
    void checkLateReleaseDate() {
        FilmValidator filmValidator = new FilmValidator();
        Film film = new Film();
        film.setName("Terminator");
        film.setDuration(20);
        film.setReleaseDate(LocalDate.of(1894, 10, 20));
        film.setDescription("About Arnold");
        assertThrows(ValidationException.class, () -> filmValidator.validate(film));
    }

    @Test
    void checkDuration() {
        FilmValidator filmValidator = new FilmValidator();
        Film film = new Film();
        film.setName("Terminator");
        film.setDuration(-20);
        film.setReleaseDate(LocalDate.of(1920, 10, 20));
        film.setDescription("About Arnold");
        assertThrows(ValidationException.class, () -> filmValidator.validate(film));
    }

    @Test
    void checkGeneratedId() {
        FilmValidator filmValidator = new FilmValidator();
        FilmRepository filmRepository = new FilmRepository();

        Film film = new Film();
        film.setName("Terminator");
        film.setDuration(20);
        film.setReleaseDate(LocalDate.of(1920, 10, 20));
        film.setDescription("About Arnold");
        if (filmValidator.validate(film)) {
            filmRepository.save(film);
        }
        assertEquals(1, film.getId());
    }

    @Test
    void checkValidate() {
        FilmValidator filmValidator = new FilmValidator();
        Film film = new Film();
        film.setName("Terminator");
        film.setDuration(20);
        film.setReleaseDate(LocalDate.of(1920, 10, 20));
        film.setDescription("About Arnold");

        assertTrue(filmValidator.validate(film));
    }
}