package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserRepository {
    private int generatorId = 0;
    private final Map<Integer, User> users = new HashMap<>();

    public int generateId() {
        return ++generatorId;
    }

    public void save(User user) {
        if (user.getId() != 0 && !users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            Integer id = generateId();
            user.setId(id);
            users.put(id, user);
        }
    }

    public void update(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            throw new ValidationException("Don't update because there isn't " + user.getId() +
                    " in the repository");
        }
    }

    public Collection<User> getUsers() {
        return users.values();
    }
}
