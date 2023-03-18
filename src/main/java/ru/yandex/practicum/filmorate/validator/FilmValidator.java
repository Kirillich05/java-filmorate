package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmValidator {
    private int id = 0;

    private boolean isEmptyName(Film film) {
        if (film.getName().length() != 0) {
            return false;
        } else {
            throw new ValidationException("Name is empty");
        }
    }

    private boolean isMaxLengthDescription(Film film) {
        if (film.getDescription().length() <= 200) {
            return false;
        } else {
            throw new ValidationException("Description size is over 200");
        }
    }

    private boolean isLateReleaseDate(Film film) {
        LocalDate cinemaBirth = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isAfter(cinemaBirth) ) {
            return true;
        } else {
            throw new ValidationException("Release date earlier than cinema creation date");
        }
    }

    private boolean isPositiveDuration(Film film) {
        if (film.getDuration() > 0) {
            return true;
        } else {
            throw new ValidationException("Duration is negative");
        }
    }

    private boolean isEmptyId(Film film) {
        if (film.getId() != 0) {
            return false;
        }
        return true;
    }

    public boolean validate(Film film) {
        if (
            !isEmptyName(film) &&
            !isMaxLengthDescription(film) &&
                    isLateReleaseDate(film) &&
                    isPositiveDuration(film)
        ) {
            if (isEmptyId(film)) {
                id = id + 1;
                film.setId(id);
            }
            return true;
        }
        return false;
    }
}
