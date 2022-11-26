package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private static final String AUTH = "Authorization";
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers(@RequestHeader(AUTH) String authHeader,
                                           HttpServletRequest request, Authentication authentication) {
        return userClient.getUsers(authHeader);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@RequestHeader(AUTH) String authHeader, @PathVariable Long userId,
                                              HttpServletRequest request, Authentication authentication) {
        return userClient.getUserById(authHeader, userId);
    }

    @ResponseBody
    @PostMapping
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<Object> create(@RequestHeader(AUTH) String authHeader, @Valid @RequestBody UserDto userDto,
                                         HttpServletRequest request, Authentication authentication) {
        log.info("Получен POST-запрос к эндпоинту: '/users' на добавление пользователя с UserDto={}", userDto);
        return userClient.create(authHeader, userDto);
    }

    @ResponseBody
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@RequestHeader(AUTH) String authHeader, @RequestBody UserDto userDto,
                                         @PathVariable Long userId, HttpServletRequest request,
                                         Authentication authentication) {
        log.info("Получен PATCH-запрос к эндпоинту: '/users' на обновление пользователя с ID={}", userId);
        return userClient.update(authHeader, userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@RequestHeader(AUTH) String authHeader, @PathVariable Long userId,
                                         HttpServletRequest request, Authentication authentication) {
        log.info("Получен DELETE-запрос к эндпоинту: '/users' на удаление пользователя с ID={}", userId);
        return userClient.delete(authHeader, userId);
    }
}