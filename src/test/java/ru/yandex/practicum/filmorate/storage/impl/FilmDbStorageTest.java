package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final Film film = Film.builder()
            .name("Harry Potter")
            .description("About magic world")
            .releaseDate(LocalDate.of(2001, 1, 10))
            .duration(120)
            .mpa(new Mpa(1, "G"))
            .genres(null)
            .likes(new LinkedHashSet<>())
            .build();

    @Test
    void save() {
        filmDbStorage.save(film);
        int filmId = filmDbStorage.getFilm(film.getId()).get().getId();

        assertEquals(film, filmDbStorage.getFilm(film.getId()).get(), "Фильм из репозитория не совпадает");
        assertNotNull(filmId, "Id фильма из репозитория не совпадает");
    }

    @Test
    void update() {
        filmDbStorage.save(film);
        film.setName("Harry Potter 2");
        film.setDuration(320);
        filmDbStorage.update(film);

        String updatedName = filmDbStorage.getFilm(film.getId()).get().getName();
        Long updatedDuration = filmDbStorage.getFilm(film.getId()).get().getDuration();
        assertEquals(film.getName(), updatedName, "Фильм из репозитория не совпадает");
        assertEquals(film.getDuration(), updatedDuration, "Фильм из репозитория не совпадает");
    }

    @Test
    void delete() {
        filmDbStorage.save(film);
        int filmId = filmDbStorage.getFilm(film.getId()).get().getId();
        assertEquals(film, filmDbStorage.getFilm(film.getId()).get(), "Фильм из репозитория не совпадает");
        assertNotNull(filmId, "Id фильма из репозитория не совпадает");

        filmDbStorage.delete(filmId);
        assertEquals(0, filmDbStorage.getFilms().size(), "Фильм не удалился");
    }

    @Test
    void getFilms() {
        filmDbStorage.save(film);
        assertNotNull(filmDbStorage.getFilms(), "Пустой список фильмов");
        assertTrue(filmDbStorage.getFilms().contains(film), "Фильмы не совпадают");
    }

    @Test
    void getFilm() {
        filmDbStorage.save(film);
        Film filmFromRepo = filmDbStorage.getFilm(film.getId()).get();
        assertEquals(film, filmFromRepo, "Фильм из репозитория не совпадает");
    }

    @Test
    void addLike() {
        User user = User.builder().build();
        user.setName("Karl");
        user.setEmail("karnel@yandex.ru");
        user.setLogin("karnel");
        user.setBirthday(LocalDate.of(2000, 10, 2));

        Film favouriteFilm = Film.builder()
                .name("Harry Potter 3")
                .description("About Azkaban and Wolf")
                .releaseDate(LocalDate.of(2008, 1, 10))
                .duration(120)
                .mpa(new Mpa(1, "G"))
                .genres(null)
                .likes(new LinkedHashSet<>())
                .build();

        userDbStorage.save(user);
        filmDbStorage.save(favouriteFilm);
        filmDbStorage.addLike(favouriteFilm.getId(), user.getId());
        assertNotNull(filmDbStorage.getTop10PopularFilms(favouriteFilm.getId()), "Пустой топ фильмов");
        assertEquals(1, filmDbStorage.getTop10PopularFilms(favouriteFilm.getId()).size(),
                "Количество фильмов в топе не совпадает");
    }

    @Test
    void removeLike() {
        User user = User.builder().build();
        user.setName("Karl");
        user.setEmail("karnel@yandex.ru");
        user.setLogin("karnel");
        user.setBirthday(LocalDate.of(2000, 10, 2));

        Film favouriteFilm = Film.builder()
                .name("Harry Potter 3")
                .description("About Azkaban and Wolf")
                .releaseDate(LocalDate.of(2008, 1, 10))
                .duration(120)
                .mpa(new Mpa(1, "G"))
                .genres(null)
                .likes(new LinkedHashSet<>())
                .build();

        userDbStorage.save(user);
        filmDbStorage.save(favouriteFilm);
        filmDbStorage.addLike(favouriteFilm.getId(), user.getId());
        filmDbStorage.removeLike(favouriteFilm.getId(), user.getId());
        assertEquals(0, filmDbStorage.getTop10PopularFilms(favouriteFilm.getId()).get(0).getLikes().size(),
                "Количество лайков не совпадает");
    }

    @Test
    void getTop10PopularFilms() {
        Film favouriteFilm = Film.builder()
                .name("Harry Potter 3")
                .description("About Azkaban and Wolf")
                .releaseDate(LocalDate.of(2008, 1, 10))
                .duration(120)
                .mpa(new Mpa(1, "G"))
                .genres(null)
                .likes(new LinkedHashSet<>())
                .build();
        filmDbStorage.save(film);
        filmDbStorage.save(favouriteFilm);

        User user1 = User.builder().build();
        user1.setName("Karl");
        user1.setEmail("karnel@yandex.ru");
        user1.setLogin("karnel");
        user1.setBirthday(LocalDate.of(2000, 10, 2));

        User user2 = User.builder().build();
        user2.setName("Mike");
        user2.setEmail("mike@yandex.ru");
        user2.setLogin("mikel");
        user2.setBirthday(LocalDate.of(1990, 2, 2));
        userDbStorage.save(user1);
        userDbStorage.save(user2);

        filmDbStorage.addLike(favouriteFilm.getId(), user1.getId());
        filmDbStorage.addLike(favouriteFilm.getId(), user2.getId());
        filmDbStorage.addLike(film.getId(), user1.getId());
        assertNotNull(filmDbStorage.getTop10PopularFilms(favouriteFilm.getId()), "Пустой топ фильмов");
        assertEquals(favouriteFilm, filmDbStorage.getTop10PopularFilms(favouriteFilm.getId()).get(0),
                "Неправильный топ");
    }
}