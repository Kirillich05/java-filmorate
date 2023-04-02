package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public String addFriend(int userId, int otherUserId) {
        User user = findUserById(userId);
        User otherUser = findUserById(otherUserId);

        if (userStorage.getUsers().contains(user) &&
                userStorage.getUsers().contains(otherUser)) {
            user.getFriends().add(otherUser.getId());
            otherUser.getFriends().add(user.getId());
            log.info("User'{}' made friend {}", userId, otherUserId);
            return "Made friends";
        } else {
            log.warn("User'{}' did not make friend {}", userId, otherUserId);
            throw new ModelNotFoundException(String.format("User %d did not make friend %d", userId, otherUserId));
        }
    }

    public User deleteFriend(int userId, int otherUserId) {
        User user = findUserById(userId);
        User otherUser = findUserById(otherUserId);

        if (userStorage.getUsers().contains(user) &&
                userStorage.getUsers().contains(otherUser)) {
            user.getFriends().remove(otherUser.getId());
            otherUser.getFriends().remove(user.getId());
            log.info("Deleted friend {} of user'{}'", otherUserId, userId);
            return otherUser;
        } else {
            log.warn("Did not delete friend {} of user'{}'", otherUserId, userId);
            throw new ModelNotFoundException(String.format("Didn't delete friend %d of user'%d", otherUserId, userId));
        }
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        User user = findUserById(userId);
        User otherUser = findUserById(otherUserId);
        Set<Integer> intersectSet = new HashSet<>(user.getFriends());
        intersectSet.retainAll(otherUser.getFriends());

        List<User> commonFriends = new ArrayList<>();
        for (Integer id : intersectSet) {
            commonFriends.add(findUserById(id));
        }
        return commonFriends;
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

    public List<User> getFriends(int id) {
        Set<Integer> friendsId = findUserById(id).getFriends();
        List<User> friends = new ArrayList<>();
        for (Integer i : friendsId) {
            friends.add(findUserById(i));
        }
        return friends;
    }
}
