package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                 // уникальный идентификатор вещи
    @NotBlank
    private String name;             // краткое название
    private String description;      // развёрнутое описание
    private Boolean available;       // статус о том, доступна или нет вещь для аренды
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;              // владелец вещи
    private Long requestId;          // если вещь была создана по запросу другого пользователя, то в этом
                                     // поле хранится ссылка на соответствующий запрос
}
