package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<Genre> getGenres() {
        String sqlQuery = "SELECT * " +
                "FROM genre";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public Optional<Genre> getGenre(int id) {
        String sqlQuery = "SELECT * " +
                "FROM genre " +
                "WHERE genre_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id));
        } catch (ModelNotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public void linkGenres(List<Film> films) {
        String sqlGenres = "SELECT film_id, g2.* " +
                "FROM film_genres " +
                "JOIN genre g2 ON g2.genre_id = film_genres.genre_id " +
                "WHERE film_id IN (:ids)";
        List<Integer> ids = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());
        Map<Integer, Film> filmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film, (a, b) -> b));
        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        SqlRowSet sqlRowSet = namedParameterJdbcTemplate.queryForRowSet(sqlGenres, parameters);
        while (sqlRowSet.next()) {
            int filmId = sqlRowSet.getInt("film_id");
            int genreId = sqlRowSet.getInt("genre_id");
            String name = sqlRowSet.getString("name");
            filmMap.get(filmId).getGenres().add(new Genre(genreId, name));
        }
        films.forEach(film -> film.getGenres().addAll(filmMap.get(film.getId()).getGenres()));
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
