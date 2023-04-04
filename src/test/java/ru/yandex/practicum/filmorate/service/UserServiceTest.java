package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    UserStorage userStorage;
    UserService userService;
    User user1;
    User user2;

    @BeforeEach
    public void init() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);

        user1 = new User();
        user1.setName("Karl");
        user1.setEmail("karnel@yandex.ru");
        user1.setLogin("karnel");
        user1.setBirthday(LocalDate.of(2000, 10, 2));
        userStorage.save(user1);

        user2 = new User();
        user2.setName("Mike");
        user2.setEmail("mike@yandex.ru");
        user2.setLogin("mikel");
        user2.setBirthday(LocalDate.of(1990, 2, 2));
        userStorage.save(user2);
    }

    @Test
    void addFriend() {
        assertEquals(2, userStorage.getUsers().size(), "Неверное количество пользователей");
        assertEquals(1, user1.getId(), "Неверный id пользователя");
        assertEquals(2, user2.getId(), "Неверный id пользователя");

        assertThrows(ModelNotFoundException.class, () -> userService.addFriend(1,3));
        assertNotNull(userService.findUserById(1), "Пользователь не найден");
        assertEquals(user1, userService.findUserById(1), "Пользователи не совпадают");

        userService.addFriend(1, 2);
        assertEquals(1, userService.getFriends(1).size(), "Неверное количество друзей");
        assertEquals(1, userService.getFriends(2).size(), "Неверное количество друзей");
        assertEquals(user2, userService.getFriends(1).get(0), "Друзья не совпадают");
        assertEquals(user1, userService.getFriends(2).get(0), "Друзья не совпадают");
    }

    @Test
    void deleteFriend() {
        assertEquals(2, userStorage.getUsers().size(), "Неверное количество пользователей");
        assertEquals(1, user1.getId(), "Неверный id пользователя");
        assertEquals(2, user2.getId(), "Неверный id пользователя");

        assertNotNull(userService.findUserById(1), "Пользователь не найден");
        assertEquals(user1, userService.findUserById(1), "Пользователи не совпадают");

        assertThrows(ModelNotFoundException.class, () -> userService.deleteFriend(1, 3));

        userService.addFriend(1, 2);
        assertEquals(1, user1.getFriends().size(), "Неверное количество друзей");
        assertEquals(1, user2.getFriends().size(), "Неверное количество друзей");

        userService.deleteFriend(1, 2);
        assertEquals(0, user1.getFriends().size(), "Неверное количество друзей");
        assertEquals(0, user2.getFriends().size(), "Неверное количество друзей");
    }

    @Test
    void getCommonFriends() {
        assertEquals(2, userStorage.getUsers().size(), "Неверное количество пользователей");
        assertEquals(1, user1.getId(), "Неверный id пользователя");
        assertEquals(2, user2.getId(), "Неверный id пользователя");

        User user3 = new User();
        user3.setName("Leo");
        user3.setEmail("leo@yandex.ru");
        user3.setLogin("leo");
        user3.setBirthday(LocalDate.of(1995, 5, 1));
        userStorage.save(user3);
        assertEquals(3, user3.getId(), "Неверный id пользователя");

        assertEquals(0, user1.getFriends().size(), "Неверное количество друзей");
        assertEquals(0, user2.getFriends().size(), "Неверное количество друзей");
        assertEquals(0, user3.getFriends().size(), "Неверное количество друзей");

        userService.addFriend(1, 2);
        userService.addFriend(1, 3);
        assertEquals(2, userService.getFriends(1).size(), "Неверное количество друзей");

        assertEquals(1, userService.getCommonFriends(2, 3).size(),
                "Неверное количество общих друзей");
        assertEquals(user1, userService.getCommonFriends(2, 3).get(0),
                "Общие друзья не совпадают");

    }

    @Test
    void findUserById() {
        assertEquals(2, userStorage.getUsers().size(), "Неверное количество пользователей");
        assertEquals(1, user1.getId(), "Неверный id пользователя");
        assertEquals(2, user2.getId(), "Неверный id пользователя");

        assertThrows(ModelNotFoundException.class, () -> userService.findUserById(3));

        assertNotNull(userService.findUserById(1), "Пользователь не найден");
        assertNotNull(userService.findUserById(2), "Пользователь не найден");
        assertEquals(user1, userService.findUserById(1), "Пользователи не совпадают");
        assertEquals(user2, userService.findUserById(2), "Пользователи не совпадают");
    }

    @Test
    void getFriends() {
        assertEquals(2, userStorage.getUsers().size(), "Неверное количество пользователей");
        assertEquals(1, user1.getId(), "Неверный id пользователя");
        assertEquals(2, user2.getId(), "Неверный id пользователя");

        assertEquals(0, user1.getFriends().size(), "Неверное количество друзей");
        assertEquals(0, user1.getFriends().size(), "Неверное количество друзей");

        userService.addFriend(1, 2);
        assertEquals(1, user1.getFriends().size(), "Неверное количество друзей");
        assertEquals(1, user2.getFriends().size(), "Неверное количество друзей");
        assertEquals(user2, userService.getFriends(1).get(0), "Друзья не совпадают");
        assertEquals(user1, userService.getFriends(2).get(0), "Друзья не совпадают");

        userService.addFriend(1, 2);
        assertEquals(1, user1.getFriends().size(), "Неверное количество друзей");
        assertEquals(1, user2.getFriends().size(), "Неверное количество друзей");
    }
}