package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.kafka.Sender;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;


@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private static final String AUTH = "Authorization";
    private final ItemRequestClient itemRequestClient;
    private final Sender sender;

    @ResponseBody
    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(AUTH) String authHeader,
                                         @RequestBody @Valid ItemRequestDto itemRequestDto,
                                         @RequestHeader(USER_ID) Long requestorId,
                                         HttpServletRequest request, Authentication authentication) {
        log.info("Получен POST-запрос к эндпоинту: '/requests' " +
                "на создание запроса вещи от пользователя с ID={}", requestorId);
        sender.sendMessage(request, authentication);
        return itemRequestClient.create(authHeader, itemRequestDto, requestorId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(AUTH) String authHeader,
                                                     @PathVariable("requestId") Long itemRequestId,
                                                     @RequestHeader(USER_ID) Long userId,
                                                     HttpServletRequest request, Authentication authentication) {
        log.info("Получен GET-запрос к эндпоинту: '/requests' на получение запроса с ID={}", itemRequestId);
        sender.sendMessage(request, authentication);
        return itemRequestClient.getItemRequestById(authHeader, userId, itemRequestId);
    }


    @GetMapping
    public ResponseEntity<Object> getOwnItemRequests(@RequestHeader(AUTH) String authHeader,
                                                     @RequestHeader(USER_ID) Long userId,
                                                     HttpServletRequest request, Authentication authentication) {
        log.info("Получен GET-запрос к эндпоинту: '/requests' на получение запросов пользователя ID={}",
                userId);
        sender.sendMessage(request, authentication);
        return itemRequestClient.getOwnItemRequests(authHeader, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(AUTH) String authHeader,
                                                     @RequestHeader(USER_ID) Long userId,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                     Integer from,
                                                     @RequestParam(required = false) Integer size,
                                                     HttpServletRequest request, Authentication authentication) {
        log.info("Получен GET-запрос к эндпоинту: '/requests/all' от пользователя с ID={} на получение всех запросов",
                userId);
        sender.sendMessage(request, authentication);
        return itemRequestClient.getAllItemRequests(authHeader, userId, from, size);
    }
}
