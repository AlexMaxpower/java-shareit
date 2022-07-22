package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.service.CheckConsistencyService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private UserService userService;
    private UserMapper mapper;
    private CheckConsistencyService checker;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper,
                          CheckConsistencyService checkConsistencyService) {
        this.userService = userService;
        this.mapper = userMapper;
        this.checker = checkConsistencyService;
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers().stream()
                .map(mapper::toUserDto)
                .collect(toList());
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        return mapper.toUserDto(userService.getUserById(userId));
    }

    @ResponseBody
    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Получен POST-запрос к эндпоинту: '/users' на добавление пользователя");
        User user = userService.create(mapper.toUser(userDto));
        return mapper.toUserDto(user);
    }

    @ResponseBody
    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("Получен PATCH-запрос к эндпоинту: '/users' на обновление пользователя с ID={}", userId);
        User user = userService.update(mapper.toUser(userDto), userId);
        return mapper.toUserDto(user);
    }

    @DeleteMapping("/{userId}")
    public UserDto delete(@PathVariable Long userId) {
        log.info("Получен DELETE-запрос к эндпоинту: '/users' на удаление пользователя с ID={}", userId);
        UserDto userDto = mapper.toUserDto(userService.delete(userId));
        checker.deleteItemsByUser(userId);
        return userDto;
    }
}
