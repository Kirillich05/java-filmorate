package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

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
    public boolean delete(int id) {
        if (!users.containsKey(id)) {
            throw new ModelNotFoundException("Don't delete user because there isn't " + id +
                    " in the repository");
        }
        users.remove(id);
        deleteUserFromFriends(id);
        return true;
    }

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public List<Integer> addFriend(int senderRequestUser, int acceptedRequestUser) {
        User user = findUserById(senderRequestUser);
        User otherUser = findUserById(acceptedRequestUser);
        user.getFriends().add(otherUser.getId());
        otherUser.getFriends().add(user.getId());
        return List.of(senderRequestUser, acceptedRequestUser);
    }

    @Override
    public List<Integer> unsubscribeFriend(int senderRequestUser, int acceptedRequestUser) {
        for (User user : users.values()) {
            user.getFriends().remove(user);
        }
        return List.of(senderRequestUser, acceptedRequestUser);
    }

    @Override
    public Collection<User> getFriends(int id) {
        Set<Integer> friendsId = findUserById(id).getFriends();
        List<User> friends = new ArrayList<>();
        for (Integer i : friendsId) {
            friends.add(findUserById(i));
        }
        return friends;
    }

    @Override
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

    private void deleteUserFromFriends(int id) {
        for (User user : users.values()) {
            user.getFriends().remove(id);
        }
    }

    public User findUserById(Integer userId) {
        return getUsers().stream()
                .filter(x -> x.getId() == userId)
                .findFirst()
                .orElseThrow(() -> {
                    return new ModelNotFoundException(String.format("User № %d не найден", userId));
                });
    }
}
