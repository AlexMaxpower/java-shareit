package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemRequestNotFoundException extends IllegalArgumentException {
    public ItemRequestNotFoundException(String message) {
        super(message);
        log.error(message);
    }
}
