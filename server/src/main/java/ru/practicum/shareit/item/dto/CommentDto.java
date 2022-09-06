package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;                // уникальный идентификатор комментария;
    private String text;            // содержимое комментария;
    @JsonIgnore
    private Item item;              // вещь, к которой относится комментарий;
    private String authorName;      // имя автора комментария;
    private LocalDateTime created;  // дата создания комментария.
}
