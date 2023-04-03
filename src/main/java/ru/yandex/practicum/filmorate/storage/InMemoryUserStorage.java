package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private int generatorId = 0;
    private final Map<Integer, User> users = new HashMap<>();

    private int generateId() {
        return ++generatorId;
    }

    @Override
    public void save(User user) {
        if (user.getId() != 0 && users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            int id = generateId();
            user.setId(id);
            users.put(id, user);
        }
    }

    @Override
    public void update(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            throw new ModelNotFoundException("Don't update because there isn't " + user.getId() +
                    " in the repository");
        }
    }

    @Override
    public String delete(int id) {
        if (!users.containsKey(id)) {
            throw new ModelNotFoundException("Don't delete user because there isn't " + id +
                    " in the repository");
        }
        users.remove(id);
        deleteUserFromFriends(id);
        return String.format("User with id %d is deleted", id);
    }

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    private void deleteUserFromFriends(int id) {
        for (User user : users.values()) {
            user.getFriends().remove(id);
        }
    }
}
