package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserValidator {
    private int id = 0;

    private boolean isEmptyEmail(User user) {
        if (user.getEmail().length() != 0 && user.getEmail().contains("@")) {
            return false;
        } else {
            throw new ValidationException("Email is empty or doesn't have '@'");
        }
    }

    private boolean isEmptyLogin(User user) {
        if (user.getLogin().length() != 0 && !user.getLogin().contains(" ")) {
            return false;
        } else {
            throw new ValidationException("Login is empty or contains spaces");
        }
    }

    private boolean isBirthInFuture(User user) {
        LocalDate now = LocalDate.now();
        if (user.getBirthday().isBefore(now) ) {
            System.out.println("here");
            return false;
        } else {
            throw new ValidationException("Birthday in the future");
        }
    }

    private boolean isEmptyName(User user) {
        if (user.getName() == null) {
            return true;
        }
        return false;
    }

    private boolean isEmptyId(User user) {
        if (user.getId() != 0) {
            return false;
        }
        return true;
    }

    public boolean validate(User user) {
        if (
                !isEmptyEmail(user) &&
                        !isEmptyLogin(user) &&
                        !isBirthInFuture(user)
        ) {
            if (isEmptyId(user)) {
                id = id + 1;
                user.setId(id);
            }
            if (isEmptyName(user)) {
                user.setName(user.getLogin());
            }
            return true;
        }
        return false;
    }
}
