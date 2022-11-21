package ru.practicum.shareit.user;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers();

    UserDto getUserById(Long id);

    UserDto create(UserDto userDto);

    ResponseEntity createUserKeyCloak(UserDto userDto);

    UserDto update(UserDto userDto, Long id);

    void delete(Long userId);

    User findUserById(Long id);
}
