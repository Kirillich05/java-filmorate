package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidatorTest {

    @Test
    void checkEmptyEmail() {
        UserValidator userValidator = new UserValidator();
        User user = new User();
        user.setEmail("");
        assertThrows(ValidationException.class, () -> userValidator.validate(user));

        user.setEmail("karnelyandex.ru");
        assertThrows(ValidationException.class, () -> userValidator.validate(user));
    }

    @Test
    void checkEmptyLogin() {
        UserValidator userValidator = new UserValidator();
        User user = new User();
        user.setEmail("karnel@yandex.ru");
        user.setLogin("");
        LocalDate birth = LocalDate.of(2000, 10, 2);
        user.setBirthday(birth);
        assertThrows(ValidationException.class, () -> userValidator.validate(user));

        user.setLogin("  ");
        assertThrows(ValidationException.class, () -> userValidator.validate(user));
    }

    @Test
    void checkBirthInFuture() {
        UserValidator userValidator = new UserValidator();
        User user = new User();
        user.setEmail("karnel@yandex.ru");
        user.setLogin("karnel");
        LocalDate dateInFuture = LocalDate.of(2100, 10, 2);
        user.setBirthday(dateInFuture);
        assertThrows(ValidationException.class, () -> userValidator.validate(user));
    }

    @Test
    void checkEmptyName() {
        UserValidator userValidator = new UserValidator();
        User user = new User();
        user.setEmail("karnel@yandex.ru");
        user.setLogin("karnel");
        LocalDate birth = LocalDate.of(2000, 10, 2);
        user.setBirthday(birth);

        userValidator.validate(user);
        assertEquals(user.getName(), "karnel");
    }

    @Test
    void checkGeneratedId() {
        UserValidator userValidator = new UserValidator();
        InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();

        User user = new User();
        user.setEmail("karnel@yandex.ru");
        user.setLogin("karnel");
        LocalDate birth = LocalDate.of(2000, 10, 2);
        user.setBirthday(birth);
        user.setName("Karl");
        if (userValidator.validate(user)) {
            inMemoryUserStorage.save(user);
        }
        assertEquals(1, user.getId());
    }

    @Test
    void checkValidate() {
        UserValidator userValidator = new UserValidator();
        User user = new User();
        user.setEmail("karnel@yandex.ru");
        user.setLogin("karnel");
        LocalDate birth = LocalDate.of(2000, 10, 2);
        user.setBirthday(birth);
        user.setName("Karl");

        assertTrue(userValidator.validate(user));
    }
}
