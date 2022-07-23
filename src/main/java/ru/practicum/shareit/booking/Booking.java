package ru.practicum.shareit.booking;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Booking {
    private Long id;                // уникальный идентификатор бронирования
    private LocalDateTime start;    // дата начала бронирования
    private LocalDateTime end;      // дата конца бронирования
    private Long itemId;            // вещь, которую пользователь бронирует
    private Long bookerId;          // пользователь, который осуществляет бронирование
    private String status;          // статус бронирования
    // WAITING — новое бронирование, ожидает одобрения,
    // APPROVED — бронирование подтверждено владельцем,
    // REJECTED — бронирование отклонено владельцем,
    // CANCELED — бронирование отменено создателем
}
