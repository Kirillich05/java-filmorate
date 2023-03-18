package ru.yandex.practicum.filmorate.validator;

public class ValidationException extends RuntimeException {

    public ValidationException(final String message) {
        super(message);
    }

    public ValidationException() {
        super();
    }
}
