package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;

import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository mockUserRepository;
    private UserService userService;
    private UserMapper mapper = new UserMapper();
    private UserDto userDto = new UserDto(1L, "Alex", "alex@alex.ru");

    @BeforeEach
    void beforeEach() {
        userService = new UserServiceImpl(mockUserRepository, mapper);
    }

    @Test
    void shouldExceptionWhenGetUserWithWrongId() {
        when(mockUserRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());
        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserById(-1L));
        Assertions.assertEquals("Пользователь с ID=-1 не найден!", exception.getMessage());
    }

    @Test
    void shouldExceptionWhenCreateUserWithExistEmail() {
        when(mockUserRepository.save(any()))
                .thenThrow(new DataIntegrityViolationException(""));
        final UserAlreadyExistsException exception = Assertions.assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.create(userDto));
        Assertions.assertEquals("Пользователь с E-mail=" + userDto.getEmail() + " уже существует!",
                exception.getMessage());
    }

    @Test
    void shouldReturnUserWhenFindUserById() {
        when(mockUserRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(mapper.toUser(userDto)));
        User user = userService.findUserById(1L);
        verify(mockUserRepository, Mockito.times(1))
                .findById(1L);
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

}
