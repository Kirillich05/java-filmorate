package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {

    UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("db") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<Integer> addFriend(int userId, int otherUserId) {
        User user = findUserById(userId);
        User otherUser = findUserById(otherUserId);

        if (userStorage.getUsers().contains(user) &&
                userStorage.getUsers().contains(otherUser)) {
            log.info("User'{}' made friend {}", userId, otherUserId);
            return userStorage.addFriend(userId, otherUserId);
        } else {
            log.warn("User'{}' did not make friend {}", userId, otherUserId);
            throw new ModelNotFoundException(String.format("User %d did not make friend %d", userId, otherUserId));
        }
    }

    public List<Integer> deleteFriend(int userId, int otherUserId) {
        User user = findUserById(userId);
        User otherUser = findUserById(otherUserId);

        if (userStorage.getUsers().contains(user) &&
                userStorage.getUsers().contains(otherUser)) {
            log.info("Deleted friend {} of user'{}'", otherUserId, userId);
            return userStorage.unsubscribeFriend(userId, otherUserId);
        } else {
            log.warn("Did not delete friend {} of user'{}'", otherUserId, userId);
            throw new ModelNotFoundException(String.format("Didn't delete friend %d of user'%d", otherUserId, userId));
        }
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        return userStorage.getCommonFriends(userId, otherUserId);
    }

    public User findUserById(Integer userId) {
        return userStorage.getUsers().stream()
                .filter(x -> x.getId() == userId)
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("There is not user by id: '{}'", userId);
                    return new ModelNotFoundException(String.format("User № %d не найден", userId));
                });
    }

    public Collection<User> getFriends(int id) {
        return userStorage.getFriends(id);
    }
}
