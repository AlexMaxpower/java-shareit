package ru.practicum.shareit.requests;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequest {
    private Long id;                  // уникальный идентификатор запроса
    private String description;       // текст запроса, содержащий описание требуемой вещи
    private Long requestorId;     // пользователь, создавший запрос
    private LocalDateTime created;    // дата и время создания запроса
}
