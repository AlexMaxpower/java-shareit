package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserKeycloakDto {
    private String id;
    private String name;
    private String email;
    @JsonIgnore
    private String password;
}
