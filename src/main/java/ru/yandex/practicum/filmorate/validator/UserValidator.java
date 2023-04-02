package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Component
@Slf4j
public class UserValidator {

    private boolean isEmptyEmail(User user) {
        if (user.getEmail().length() != 0 && user.getEmail().contains("@")) {
            return false;
        } else {
            log.warn("Email is empty or doesn't have '@': {}", user);
            throw new ValidationException("Email is empty or doesn't have '@'");
        }
    }

    private boolean isEmptyLogin(User user) {
        if (!user.getLogin().isBlank()) {
            return false;
        } else {
            log.warn("Login is empty or contains spaces: {}", user);
            throw new ValidationException("Login is empty or contains spaces");
        }
    }

    private boolean isBirthInFuture(User user) {
        LocalDate now = LocalDate.now();
        if (user.getBirthday().isBefore(now) ) {
            return false;
        } else {
            log.warn("Birthday in the future");
            throw new ValidationException("Birthday in the future");
        }
    }

    private boolean isEmptyName(User user) {
        return user.getName().isBlank();
    }

    public boolean validate(User user) {
        if (!isEmptyEmail(user) && !isEmptyLogin(user) && !isBirthInFuture(user)) {
            if (user.getName() == null || isEmptyName(user)) {
                user.setName(user.getLogin());
            }
            return true;
        }
        log.warn("User isn't correct: {}", user);
        return false;
    }
}
