package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {

    void save(User user);

    void update(User user);

    boolean delete(int id);

    Collection<User> getUsers();

    List<Integer> addFriend(int senderRequestUser, int acceptedRequestUser);

    List<Integer> unsubscribeFriend(int senderRequestUser, int acceptedRequestUser);

    Collection<User> getFriends(int id);

    List<User> getCommonFriends(int userId, int otherUserId);
}
