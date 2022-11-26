package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.kafka.Sender;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private static final String AUTH = "Authorization";
    private final BookingClient bookingClient;
    private final Sender sender;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(AUTH) String authHeader,
                                              @RequestHeader(USER_ID) Long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                              Integer from,
                                              @RequestParam(required = false) Integer size,
                                              HttpServletRequest request, Authentication authentication) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        sender.sendMessage(request, authentication);
        return bookingClient.getBookings(authHeader, userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsOwner(@RequestHeader(AUTH) String authHeader,
                                                   @RequestParam(name = "state", defaultValue = "all")
                                                   String stateParam,
                                                   @RequestHeader(USER_ID) Long userId,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(required = false) Integer size,
                                                   HttpServletRequest request, Authentication authentication) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Получен GET-запрос к эндпоинту: '/bookings/owner' на получение " +
                "списка всех бронирований вещей пользователя с ID={} с параметром STATE={}", userId, state);
        sender.sendMessage(request, authentication);
        return bookingClient.getBookingsOwner(authHeader, userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(AUTH) String authHeader,
                                         @RequestHeader(USER_ID) Long userId,
                                         @RequestBody @Valid BookItemRequestDto requestDto,
                                         HttpServletRequest request, Authentication authentication) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        sender.sendMessage(request, authentication);
        return bookingClient.create(authHeader, userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(AUTH) String authHeader,
                                             @RequestHeader(USER_ID) Long userId,
                                             @PathVariable Long bookingId,
                                             HttpServletRequest request, Authentication authentication) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        sender.sendMessage(request, authentication);
        return bookingClient.getBooking(authHeader, userId, bookingId);
    }

    @ResponseBody
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(@RequestHeader(AUTH) String authHeader,
                                         @PathVariable Long bookingId,
                                         @RequestHeader(USER_ID) Long userId, @RequestParam Boolean approved,
                                         HttpServletRequest request, Authentication authentication) {
        log.info("Получен PATCH-запрос к эндпоинту: '/bookings' на обновление статуса бронирования с ID={}",
                bookingId);
        sender.sendMessage(request, authentication);
        return bookingClient.update(authHeader, bookingId, userId, approved);
    }
}
