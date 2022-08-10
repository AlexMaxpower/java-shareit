package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public class UserNotFoundException extends IllegalArgumentException {
    public UserNotFoundException(String message) {
        super(message);
        log.error(message);
    }
}
