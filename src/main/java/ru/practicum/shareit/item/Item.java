package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class Item {
    private Long id;                 // уникальный идентификатор вещи
    @NotBlank
    private String name;             // краткое название
    private String description;      // развёрнутое описание
    private Boolean available;       // статус о том, доступна или нет вещь для аренды
    private Long ownerId;            // владелец вещи
    private Long requestId;          // если вещь была создана по запросу другого пользователя, то в этом
                                     // поле хранится ссылка на соответствующий запрос
}
