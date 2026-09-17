package ru.yandex.practicum;

import ru.yandex.practicum.GameException;

public class InvalidWordException extends GameException {
    public InvalidWordException(String message) {
        super(message);
    }
}