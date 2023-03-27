package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Component
@Slf4j
public class FilmValidator {

    private boolean isEmptyName(Film film) {
        if (film.getName().length() != 0) {
            return false;
        } else {
            log.warn("film name is empty: {}", film);
            throw new ValidationException("Name is empty");
        }
    }

    private boolean isMaxLengthDescription(Film film) {
        if (film.getDescription().length() <= 200) {
            return false;
        } else {
            log.warn("Description size is over 200: {}", film);
            throw new ValidationException("Description size is over 200");
        }
    }

    private boolean isLateReleaseDate(Film film) {
        LocalDate cinemaBirth = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isAfter(cinemaBirth) ) {
            return true;
        } else {
            log.warn("Release date earlier than cinema creation date");
            throw new ValidationException("Release date earlier than cinema creation date");
        }
    }

    private boolean isPositiveDuration(Film film) {
        if (film.getDuration() > 0) {
            return true;
        } else {
            log.warn("Duration is negative: {}", film);
            throw new ValidationException("Duration is negative");
        }
    }

    public boolean validate(Film film) {
        if (!isEmptyName(film) && !isMaxLengthDescription(film) &&
                isLateReleaseDate(film) && isPositiveDuration(film)) {
            return true;
        }
        log.warn("Film isn't correct: {}", film);
        return false;
    }
}
