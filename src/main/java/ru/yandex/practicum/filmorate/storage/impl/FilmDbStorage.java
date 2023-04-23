package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
@Qualifier("db")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Film film) {
        String sqlQuery = "insert into films(name, description, release_date, duration, mpa_id) " +
                "values (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        setGenre(film);
        film.setId(keyHolder.getKey().intValue());
    }

    @Override
    public void update(Film film) {
        String sqlQuery = "UPDATE films SET " +
                "name = ?, description = ?, duration = ?,  release_date = ?, mpa_id = ? " +
                "WHERE film_id = ?";
        deleteGenres(film);
        setGenre(film);
        int res = jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(),
                film.getDuration(), film.getReleaseDate(),
                film.getMpa().getId(), film.getId());
        if (res < 1) throw new ModelNotFoundException("Don't update because there isn't " + film.getId() +
                " in the repository");
    }

    @Override
    public boolean delete(int id) {
        String genreSql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(genreSql, id);

        String sqlQuery = "DELETE FROM films WHERE film_id = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public List<Film> getFilms() {
        String sqlQuery = "SELECT f.*, m.* " +
                "FROM films AS f " +
                "JOIN mpa AS m ON m.mpa_id = f.mpa_id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Optional<Film> getFilm(int id) {
        String sqlQuery = "SELECT films.*, m.* " +
                "FROM films " +
                "join mpa AS m ON m.mpa_id = films.mpa_id " +
                "WHERE films.film_id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
            return Optional.ofNullable(film);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sqlQuery = "MERGE INTO films_likes (film_id, user_id) " +
                "VALUES (?, ?) ";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        String sqlQuery = "DELETE FROM films_likes " +
                "WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("film_id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        LocalDate releaseDate = resultSet.getDate("release_date").toLocalDate();
        long duration = resultSet.getLong("duration");
        Mpa mpa = new Mpa(resultSet.getInt("mpa.mpa_id"), resultSet.getString("mpa.name"));

        return new Film(id, name, description, releaseDate, duration, mpa, new LinkedHashSet<>(), new LinkedHashSet<>());
    }

    private void setGenre(Film film) {
        if (film.getGenres() == null) {
            film.setGenres(new LinkedHashSet<>());
        } else {
            String sqlQuery = "INSERT INTO film_genres (film_id, genre_id) " +
                    "values (?, ?)";
            jdbcTemplate.batchUpdate(sqlQuery, film.getGenres(), film.getGenres().size(),
                    (stmt, genre) -> {
                       stmt.setInt(1, film.getId());
                       stmt.setInt(2, genre.getId());
                    });
        }
    }

    private void deleteGenres(Film film) {
        String sqlQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    @Override
    public List<Film> getTop10PopularFilms(int count) {
        String sqlQuery = "SELECT f.film_id, f.name, f.description, f.duration, " +
                "f.release_date, mpa.mpa_id, mpa.name " +
                "FROM films as f " +
                "LEFT JOIN mpa ON f.mpa_id = mpa.mpa_id " +
                "LEFT JOIN films_likes AS fl ON f.film_id = fl.film_id " +
                "GROUP BY f.film_id, fl.film_id IN (SELECT film_id " +
                "FROM films_likes) " +
                "ORDER BY COUNT(fl.film_id) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);

    }


}
