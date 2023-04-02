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

    public void save(User user) {
        if (user.getId() != 0 && users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            int id = generateId();
            user.setId(id);
            users.put(id, user);
        }
    }

    public void update(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            throw new ModelNotFoundException("Don't update because there isn't " + user.getId() +
                    " in the repository");
        }
    }

    public Collection<User> getUsers() {
        return users.values();
    }
}
