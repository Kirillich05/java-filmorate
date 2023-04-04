package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    void save(User user);

    void update(User user);

    String delete(int id);

    Collection<User> getUsers();

}
