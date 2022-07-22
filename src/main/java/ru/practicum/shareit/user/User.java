package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class User {
    private Long id;        // уникальный идентификатор пользователя
    @NotBlank
    private String name;    // имя или логин пользователя
    @Email
    @NotBlank
    private String email;   // адрес электронной почты
}
