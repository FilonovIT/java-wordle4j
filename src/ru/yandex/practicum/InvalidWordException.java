package ru.yandex.practicum;

import ru.yandex.practicum.GameException;

public class InvalidWordException extends GameException {
    public InvalidWordException(String message) {
        super(message);
    }
}

//            |-> вода холодная    - 00310.3|
//            |-> вода горячая     - 00184.8|
//            |-> электричество t1 - 06352.1|
//            |-> электричество t2 - 01869.2|
//            |-> электрич. общее  - 08221.3|
//            |-----------------------------|