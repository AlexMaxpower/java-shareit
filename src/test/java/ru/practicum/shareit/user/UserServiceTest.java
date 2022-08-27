package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.service.CheckConsistencyService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {
    private final UserService userService;
    private final UserMapper mapper;
    private final CheckConsistencyService checker;
    private User user = new User(1L, "User", "first@first.ru");

    @Test
    void shouldReturnUserWhenGetUserById() {
        UserDto returnUserDto = userService.create(mapper.toUserDto(user));
        assertThat(returnUserDto.getName(), equalTo(user.getName()));
        assertThat(returnUserDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void shouldExceptionWhenDeleteUserWithWrongId() {
        UserNotFoundException exp = assertThrows(UserNotFoundException.class, () -> userService.delete(10L));
        assertEquals("Пользователь с ID=10 не найден!", exp.getMessage());
    }

    @Test
    void shouldDeleteUser() {
        User user = new User(10L, "Ten", "ten@ten.ru");
        UserDto returnUserDto = userService.create(mapper.toUserDto(user));
        List<UserDto> listUser = userService.getUsers();
        int size = listUser.size();
        userService.delete(returnUserDto.getId());
        listUser = userService.getUsers();
        assertThat(listUser.size(), equalTo(size - 1));
    }

    @Test
    void shouldUpdateUser() {
        UserDto returnUserDto = userService.create(mapper.toUserDto(user));
        returnUserDto.setName("NewName");
        returnUserDto.setEmail("new@email.ru");
        userService.update(returnUserDto, returnUserDto.getId());
        UserDto updateUserDto = userService.getUserById(returnUserDto.getId());
        assertThat(updateUserDto.getName(), equalTo("NewName"));
        assertThat(updateUserDto.getEmail(), equalTo("new@email.ru"));
    }

    @Test
    void shouldExceptionWhenUpdateUserWithExistEmail() {
        user = new User(2L, "User2", "second@second.ru");
        userService.create(mapper.toUserDto(user));
        User newUser = new User(3L, "User3", "third@third.ru");
        UserDto returnUserDto = userService.create(mapper.toUserDto(newUser));
        Long id = returnUserDto.getId();
        returnUserDto.setId(null);
        returnUserDto.setEmail("second@second.ru");
        final UserAlreadyExistsException exception = Assertions.assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.update(returnUserDto, id));
        Assertions.assertEquals("Пользователь с E-mail=" + returnUserDto.getEmail() + " уже существует!",
                exception.getMessage());
    }
}

