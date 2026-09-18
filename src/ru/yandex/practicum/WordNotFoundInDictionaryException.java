package ru.yandex.practicum;

import ru.yandex.practicum.GameException;

public class WordNotFoundInDictionaryException extends GameException {
    public WordNotFoundInDictionaryException(String message) {
        super(message);
    }
}