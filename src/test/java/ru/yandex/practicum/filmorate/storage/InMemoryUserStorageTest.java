package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserStorageTest {

    UserStorage userStorage;
    User user1;
    User user2;

    @BeforeEach
    public void init() {
        userStorage = new InMemoryUserStorage();

        user1 = new User();
        user1.setName("Karl");
        user1.setEmail("karnel@yandex.ru");
        user1.setLogin("karnel");
        user1.setBirthday(LocalDate.of(2000, 10, 2));

        user2 = new User();
        user2.setName("Mike");
        user2.setEmail("mike@yandex.ru");
        user2.setLogin("mikel");
        user2.setBirthday(LocalDate.of(1990, 2, 2));
    }

    @Test
    void generateId() {
        userStorage.save(user1);

        assertEquals(1, user1.getId(), "Не верный id пользователя");

        userStorage.save(user2);
        assertEquals(2, user2.getId(), "Не верный id пользователя");
    }

    @Test
    void save() {
        userStorage.save(user1);
        assertEquals(1, user1.getId(), "Не верный id пользователя");
        assertEquals(1, userStorage.getUsers().size(),
                "Неверное количество пользователей в репозитории");

        userStorage.save(user2);
        assertEquals(2, user2.getId(), "Не верный id пользователя");
        assertEquals(2, userStorage.getUsers().size(),
                "Неверное количество пользователей в репозитории");

        userStorage.save(user1);
        assertEquals(1, user1.getId(), "Не верный id пользователя");
        assertEquals(2, userStorage.getUsers().size(),
                "Неверное количество пользователей в репозитории");
    }

    @Test
    void update() {
        userStorage.save(user1);
        assertEquals(1, user1.getId(), "Не верный id пользователя");
        assertEquals(1, userStorage.getUsers().size(),
                "Неверное количество пользователей в репозитории");

        assertThrows(ModelNotFoundException.class, () -> userStorage.update(user2));

        user1.setEmail("karnel2000@yandex.ru");
        userStorage.update(user1);
        assertEquals(1, user1.getId(), "Не верный id пользователя");
        assertEquals(1, userStorage.getUsers().size(),
                "Неверное количество пользователей в репозитории");
        List<User> users = new ArrayList<>(userStorage.getUsers());
        assertEquals(user1, users.get(0),
                "Не совпадают обновленные пользователи в репозитории");
        assertEquals("karnel2000@yandex.ru", users.get(0).getEmail(),
                "Не совпадают emails после обновления");
    }

    @Test
    void getUsers() {
        userStorage.save(user1);
        assertEquals(1, user1.getId(), "Не верный id пользователя");
        assertNotNull(userStorage.getUsers(), "Пользователь не найден");
        assertEquals(1, userStorage.getUsers().size(),
                "Неверное количество пользователей в репозитории");

        userStorage.save(user2);
        assertEquals(2, user2.getId(), "Не верный id пользователя");
        assertEquals(2, userStorage.getUsers().size(),
                "Неверное количество пользователей в репозитории");
    }
}