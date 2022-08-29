package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;

@Component
public class ItemRequestMapper {

    private UserMapper userMapper;
    private UserService userService;
    private ItemService itemService;

    @Autowired
    public ItemRequestMapper(UserMapper userMapper, UserService userService, ItemService itemService) {
        this.userMapper = userMapper;
        this.userService = userService;
        this.itemService = itemService;
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                userMapper.toUserDto(itemRequest.getRequestor()),
                itemRequest.getCreated(),
                itemService.getItemsByRequestId(itemRequest.getId())
        );
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto, Long requestorId, LocalDateTime created) {
        return new ItemRequest(
                null,
                itemRequestDto.getDescription(),
                userService.findUserById(requestorId),
                created
        );
    }
}
