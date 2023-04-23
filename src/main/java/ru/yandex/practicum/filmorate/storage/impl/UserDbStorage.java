package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Component
@Qualifier("db")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(User user) {
        String sqlQuery = "insert into users(email, login, name, birthday) " +
                    "values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
    }

    @Override
    public void update(User user) {
        String sqlQuery = "update users set " +
                    "email= ?, login = ?, name = ?, birthday = ? " +
                    "where user_id = ?";
        int res = jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(),
                user.getName(),user.getBirthday(), user.getId());
        if (res < 1) throw new ModelNotFoundException("Don't update because there isn't " + user.getId() +
                " in the repository");
    }

    @Override
    public boolean delete(int id) {
        String sqlQuery = "delete from users where user_id = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public Collection<User> getUsers() {
        String sqlQuery = "select * from USERS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public List<Integer> addFriend(int senderRequestUser, int acceptedRequestUser) {
        String sqlQuery = "MERGE INTO friendship (user_id, friend_id) " +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, senderRequestUser, acceptedRequestUser);
        return List.of(senderRequestUser, acceptedRequestUser);
    }

    @Override
    public List<Integer> unsubscribeFriend(int senderRequestUser, int acceptedRequestUser) {
        String sqlQuery = "DELETE FROM friendship " +
                "WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery, senderRequestUser, acceptedRequestUser);
        return List.of(senderRequestUser, acceptedRequestUser);
    }

    @Override
    public List<User> getFriends(int id) {
        String sqlQuery = "SELECT users.user_id, email, login, name, birthday " +
                "FROM users " +
                "LEFT JOIN friendship AS frndshp ON users.user_id = frndshp.friend_id " +
                "WHERE frndshp.user_id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id);
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherUserId) {
        String sqlQuery = "SELECT users.user_id, email, login, name, birthday " +
                "FROM friendship AS frndshp " +
                "LEFT JOIN users ON users.user_id = frndshp.friend_id " +
                "WHERE frndshp.user_id = ? AND frndshp.friend_id IN ( " +
                "SELECT friend_id " +
                "FROM friendship AS frndshp " +
                "LEFT JOIN users ON users.user_id = frndshp.friend_id " +
                "WHERE frndshp.user_id = ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId, otherUserId);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}
