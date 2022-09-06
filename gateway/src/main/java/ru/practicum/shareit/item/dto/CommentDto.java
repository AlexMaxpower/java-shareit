package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;                // уникальный идентификатор комментария;
    @NotBlank
    private String text;            // содержимое комментария;
    private String authorName;      // имя автора комментария;
    private LocalDateTime created;  // дата создания комментария.
}
