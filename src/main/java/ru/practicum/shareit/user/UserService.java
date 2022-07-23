package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class UserService {
    private UserStorage userStorage;
    private UserMapper mapper;

    @Autowired
    public UserService(@Qualifier("InMemoryUserStorage") UserStorage userStorage, UserMapper userMapper) {
        this.userStorage = userStorage;
        this.mapper = userMapper;
    }

    public List<UserDto> getUsers() {
        return userStorage.getUsers().stream()
                .map(mapper::toUserDto)
                .collect(toList());
    }


    public UserDto getUserById(Long id) {
        return mapper.toUserDto(userStorage.getUserById(id));
    }

    public UserDto create(UserDto userDto) {
        return mapper.toUserDto(userStorage.create(mapper.toUser(userDto)));
    }

    public UserDto update(UserDto userDto, Long id) {
        if (userDto.getId() == null) {
            userDto.setId(id);
        }
        return mapper.toUserDto(userStorage.update(mapper.toUser(userDto)));
    }

    public UserDto delete(Long userId) {
        return mapper.toUserDto(userStorage.delete(userId));
    }
}