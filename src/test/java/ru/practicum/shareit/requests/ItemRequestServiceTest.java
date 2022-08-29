package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    private final ItemRequestService itemRequestService;

    private final UserService userService;
    private UserDto userDto1 = new UserDto(101L, "AlexOne", "alexone@alex.ru");
    private UserDto userDto2 = new UserDto(102L, "AlexTwo", "alextwo@alex.ru");

    private ItemRequestDto itemRequestDto = new ItemRequestDto(100L, "ItemRequest description",
            userDto1, LocalDateTime.of(2022, 1, 2, 3, 4, 5), null);

    @Test
    void shouldCreateItemRequest() {
        UserDto newUserDto = userService.create(userDto1);
        ItemRequestDto returnRequestDto = itemRequestService.create(itemRequestDto, newUserDto.getId(),
                LocalDateTime.of(2022, 1, 2, 3, 4, 5));
        assertThat(returnRequestDto.getDescription(), equalTo(itemRequestDto.getDescription()));
    }

    @Test
    void shouldExceptionWhenCreateItemRequestWithWrongUserId() {
        UserNotFoundException exp = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.create(itemRequestDto, -2L,
                        LocalDateTime.of(2022, 1, 2, 3, 4, 5)));
        assertEquals("Пользователь с ID=-2 не найден!", exp.getMessage());
    }

    @Test
    void shouldExceptionWhenGetItemRequestWithWrongId() {
        UserDto firstUserDto = userService.create(userDto1);
        ItemRequestNotFoundException exp = assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.getItemRequestById(-2L, firstUserDto.getId()));
        assertEquals("Запрос с ID=-2 не найден!", exp.getMessage());
    }

    @Test
    void shouldReturnAllItemRequestsWhenSizeNotNullAndNull() {
        UserDto firstUserDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemRequestDto returnOneRequestDto = itemRequestService.create(itemRequestDto, newUserDto.getId(),
                LocalDateTime.of(2023, 1, 2, 3, 4, 5));
        ItemRequestDto returnTwoRequestDto = itemRequestService.create(itemRequestDto, newUserDto.getId(),
                LocalDateTime.of(2024, 1, 2, 3, 4, 5));
        List<ItemRequestDto> listItemRequest = itemRequestService.getAllItemRequests(firstUserDto.getId(),
                0, 10);
        assertThat(listItemRequest.size(), equalTo(2));
    }

    @Test
    void shouldReturnAllItemRequestsWhenSizeNull() {
        UserDto firstUserDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemRequestDto returnOneRequestDto = itemRequestService.create(itemRequestDto, newUserDto.getId(),
                LocalDateTime.of(2023, 1, 2, 3, 4, 5));
        ItemRequestDto returnTwoRequestDto = itemRequestService.create(itemRequestDto, newUserDto.getId(),
                LocalDateTime.of(2024, 1, 2, 3, 4, 5));
        List<ItemRequestDto> listItemRequest = itemRequestService.getAllItemRequests(firstUserDto.getId(),
                0, null);
        assertThat(listItemRequest.size(), equalTo(2));
    }

    @Test
    void shouldReturnOwnItemRequests() {
        UserDto firstUserDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemRequestDto returnOneRequestDto = itemRequestService.create(itemRequestDto, newUserDto.getId(),
                LocalDateTime.of(2023, 1, 2, 3, 4, 5));
        ItemRequestDto returnTwoRequestDto = itemRequestService.create(itemRequestDto, newUserDto.getId(),
                LocalDateTime.of(2024, 1, 2, 3, 4, 5));
        List<ItemRequestDto> listItemRequest = itemRequestService.getOwnItemRequests(newUserDto.getId());
        System.out.println(listItemRequest.toString());
        assertThat(listItemRequest.size(), equalTo(2));
    }

    @Test
    void shouldReturnItemRequestById() {
        UserDto firstUserDto = userService.create(userDto1);
        ItemRequestDto newItemRequestDto = itemRequestService.create(itemRequestDto, firstUserDto.getId(),
                LocalDateTime.of(2023, 1, 2, 3, 4, 5));
        ItemRequestDto returnItemRequestDto = itemRequestService.getItemRequestById(newItemRequestDto.getId(),
                firstUserDto.getId());
        assertThat(returnItemRequestDto.getDescription(), equalTo(itemRequestDto.getDescription()));
    }
}