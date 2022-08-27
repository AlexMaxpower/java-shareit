package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CheckConsistencyServiceTest {
    private final CheckConsistencyService checker;

    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private User user = new User(1L, "User", "first@first.ru");
    private User user2 = new User(2L, "Second", "second@second.ru");
    private Item item = new Item(1L, "Item1", "Description1", true, user, null);

    @Test
    void shouldReturnTrueWhenExistUser() {
        UserDto newUserDto = userService.create(userMapper.toUserDto(user));
        assertTrue(checker.isExistUser(newUserDto.getId()));
    }

    @Test
    void shouldReturnTrueWhenItemAvailable() {
        UserDto newUserDto = userService.create(userMapper.toUserDto(user));
        ItemDto newItemDto = itemService.create(itemMapper.toItemDto(item), newUserDto.getId());
        assertTrue(checker.isAvailableItem(newItemDto.getId()));
    }

    @Test
    void shouldReturnTrueWhenIsItemOwner() {
        UserDto newUserDto = userService.create(userMapper.toUserDto(user));
        ItemDto newItemDto = itemService.create(itemMapper.toItemDto(item), newUserDto.getId());
        assertTrue(checker.isItemOwner(newItemDto.getId(), newUserDto.getId()));
    }

    @Test
    void shouldReturnBookingWithUserBookedItem() {
        UserDto firstUserDto = userService.create(userMapper.toUserDto(user));
        UserDto secondUserDto = userService.create(userMapper.toUserDto(user2));
        ItemDto newItemDto = itemService.create(itemMapper.toItemDto(item), firstUserDto.getId());
        BookingInputDto bookingInputDto = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(3)
        );
        BookingDto bookingDto = bookingService.create(bookingInputDto, secondUserDto.getId());
        bookingService.update(bookingDto.getId(), firstUserDto.getId(), true);
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(checker.getBookingWithUserBookedItem(newItemDto.getId(),
                secondUserDto.getId()).getId(), bookingDto.getId());
    }

}