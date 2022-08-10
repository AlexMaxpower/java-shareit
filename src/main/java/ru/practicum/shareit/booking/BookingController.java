package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.service.CheckConsistencyService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private BookingService service;
    private CheckConsistencyService checker;

    @Autowired
    public BookingController(BookingService bookingService, CheckConsistencyService checkConsistencyService) {
        this.service = bookingService;
        this.checker = checkConsistencyService;
    }

    @ResponseBody
    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingInputDto bookingInputDto,
                             @RequestHeader(USER_ID) Long bookerId) {
        log.info("Получен POST-запрос к эндпоинту: '/bookings' " +
                "на создание бронирования от пользователя с ID={}", bookerId);
        BookingDto newBookingDto = null;
        if (checker.isExistUser(bookerId)) {
            newBookingDto = service.create(bookingInputDto, bookerId);
        }
        return newBookingDto;
    }

    @ResponseBody
    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable Long bookingId,
                             @RequestHeader(USER_ID) Long userId, @RequestParam Boolean approved) {
        log.info("Получен PATCH-запрос к эндпоинту: '/bookings' на обновление статуса бронирования с ID={}", bookingId);
        BookingDto bookingDto = null;
        if (checker.isExistUser(userId)) {
            bookingDto = service.update(bookingId, userId, approved);
        }
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId, @RequestHeader(USER_ID) Long userId) {
        log.info("Получен GET-запрос к эндпоинту: '/bookings' на получение бронирования с ID={}", bookingId);
        return service.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookings(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                        @RequestHeader(USER_ID) Long userId) {
        log.info("Получен GET-запрос к эндпоинту: '/bookings' на получение " +
                "списка всех бронирований пользователя с ID={} с параметром STATE={}", userId, state);
        if (checker.isExistUser(userId)) {
            return service.getBookings(state, userId);
        } else {
            throw new UserNotFoundException("Пользователь с ID=" + userId + " не найден!");
        }
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsOwner(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                             @RequestHeader(USER_ID) Long userId) {
        log.info("Получен GET-запрос к эндпоинту: '/bookings/owner' на получение " +
                "списка всех бронирований вещей пользователя с ID={} с параметром STATE={}", userId, state);
        if (checker.isExistUser(userId)) {
            return service.getBookingsOwner(state, userId);
        } else {
            throw new UserNotFoundException("Пользователь с ID=" + userId + " не найден!");
        }
    }
}
