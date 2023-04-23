package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final UserDbStorage userDbStorage;

    @Test
    void save() {
        User user = User.builder()
                .name("Karl")
                .email("karnel@yandex.ru")
                .login("karnel")
                .birthday(LocalDate.of(2000, 10, 2))
                .build();
        userDbStorage.save(user);
        int userId = user.getId();

        assertTrue(userDbStorage.getUsers().contains(user));
        assertNotNull(userId, "Id пользователя из репозитория не совпадает");
    }

    @Test
    void update() {
        User user = User.builder()
                .name("Karl")
                .email("karnel@yandex.ru")
                .login("karnel")
                .birthday(LocalDate.of(2000, 10, 2))
                .build();
        userDbStorage.save(user);
        user.setEmail("new@yandex.ru");
        user.setName("Mika");
        userDbStorage.update(user);

        List<User> users = new ArrayList<>(userDbStorage.getUsers());
        String updatedName = users.get(0).getName();
        String updatedEmail = users.get(0).getEmail();
        assertEquals(user.getName(), updatedName, "Обновленное имя пользователя не совпадает");
        assertEquals(user.getEmail(), updatedEmail, "Обновленная почта пользователя не совпадает");
    }

    @Test
    void delete() {
        User user = User.builder()
                .name("Karl")
                .email("karnel@yandex.ru")
                .login("karnel")
                .birthday(LocalDate.of(2000, 10, 2))
                .build();
        userDbStorage.save(user);
        userDbStorage.delete(user.getId());

        List<User> users = new ArrayList<>(userDbStorage.getUsers());
        assertFalse(users.contains(user));
    }

    @Test
    void getUsers() {
        User user = User.builder()
                .name("Karl")
                .email("karnel@yandex.ru")
                .login("karnel")
                .birthday(LocalDate.of(2000, 10, 2))
                .build();
        userDbStorage.save(user);
        List<User> users = new ArrayList<>(userDbStorage.getUsers());

        assertNotNull(users);
        assertTrue(users.contains(user));
    }

    @Test
    void addFriend() {
        User user = User.builder()
                .name("Karl")
                .email("karnel@yandex.ru")
                .login("karnel")
                .birthday(LocalDate.of(2000, 10, 2))
                .build();
        User friend = User.builder()
                .name("Mike")
                .email("mike@yandex.ru")
                .login("mike")
                .birthday(LocalDate.of(2010, 5, 2))
                .build();
        userDbStorage.save(user);
        userDbStorage.save(friend);
        userDbStorage.addFriend(user.getId(), friend.getId());

        assertEquals(friend ,userDbStorage.getFriends(user.getId()).get(0),
                "Друзья не совпадают");
    }

    @Test
    void unsubscribeFriend() {
        User user = User.builder()
                .name("Karl")
                .email("karnel@yandex.ru")
                .login("karnel")
                .birthday(LocalDate.of(2000, 10, 2))
                .build();
        User friend = User.builder()
                .name("Mike")
                .email("mike@yandex.ru")
                .login("mike")
                .birthday(LocalDate.of(2010, 5, 2))
                .build();
        userDbStorage.save(user);
        userDbStorage.save(friend);
        userDbStorage.addFriend(user.getId(), friend.getId());

        assertEquals(friend ,userDbStorage.getFriends(user.getId()).get(0),
                "Друзья не совпадают");

        userDbStorage.unsubscribeFriend(user.getId(), friend.getId());

        assertEquals(0 ,userDbStorage.getFriends(user.getId()).size(),
                "Количество друзей не совпадает");
    }

    @Test
    void getFriends() {
        User user = User.builder()
                .name("Karl")
                .email("karnel@yandex.ru")
                .login("karnel")
                .birthday(LocalDate.of(2000, 10, 2))
                .build();
        User friend = User.builder()
                .name("Mike")
                .email("mike@yandex.ru")
                .login("mike")
                .birthday(LocalDate.of(2010, 5, 2))
                .build();
        userDbStorage.save(user);
        userDbStorage.save(friend);
        userDbStorage.addFriend(user.getId(), friend.getId());

        assertNotNull(userDbStorage.getFriends(user.getId()));
        assertEquals(friend ,userDbStorage.getFriends(user.getId()).get(0),
                "Друзья не совпадают");
        assertEquals(1 ,userDbStorage.getFriends(user.getId()).size(),
                "Количество друзей не совпадает");
    }

    @Test
    void getCommonFriends() {
        User user = User.builder()
                .name("Karl")
                .email("karnel@yandex.ru")
                .login("karnel")
                .birthday(LocalDate.of(2000, 10, 2))
                .build();
        User friend1 = User.builder()
                .name("Mike")
                .email("mike@yandex.ru")
                .login("mike")
                .birthday(LocalDate.of(2010, 5, 2))
                .build();
        User friend2 = User.builder()
                .name("Leo")
                .email("leo@yandex.ru")
                .login("leo")
                .birthday(LocalDate.of(2005, 2, 2))
                .build();
        userDbStorage.save(user);
        userDbStorage.save(friend1);
        userDbStorage.save(friend2);
        userDbStorage.addFriend(user.getId(), friend1.getId());
        userDbStorage.addFriend(user.getId(), friend2.getId());
        userDbStorage.addFriend(friend1.getId(), friend2.getId());
        assertEquals(friend2, userDbStorage.getCommonFriends(user.getId(), friend1.getId()).get(0),
                "Общие друзья не совпадают");
        assertEquals(1, userDbStorage.getCommonFriends(user.getId(), friend1.getId()).size(),
                "Список общих друзей не совпадают");

    }
}