package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.keycloak.KeycloakUtils;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserKeycloakDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;
    private final KeycloakUtils keycloakUtils;

    @Autowired
    public UserServiceImpl(UserRepository repository, UserMapper userMapper, KeycloakUtils keycloakUtils) {
        this.repository = repository;
        this.mapper = userMapper;
        this.keycloakUtils = keycloakUtils;
    }

    @Override
    public List<UserDto> getUsers() {
        return repository.findAll().stream()
                .map(mapper::toUserDto)
                .collect(toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        return mapper.toUserDto(repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID=" + id + " не найден!")));
    }

    @Override
    public UserDto create(UserDto userDto) {
        if (userDto.getPassword() == null) {
            userDto.setPassword("password");
        }
        UserKeycloakDto userKeycloakDto = keycloakUtils.createKeycloakUser(userDto);
        userDto.setUuid(userKeycloakDto.getId());
        try {
            return mapper.toUserDto(repository.save(mapper.toUser(userDto)));
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException("Пользователь с E-mail=" +
                    userDto.getEmail() + " уже существует!");
        }
    }

    @Override
    public UserDto update(UserDto userDto, Long id) {
        if (userDto.getId() == null) {
            userDto.setId(id);
        }
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID=" + id + " не найден!"));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if ((userDto.getEmail() != null) && (userDto.getEmail() != user.getEmail())) {
            if (repository.findByEmail(userDto.getEmail())
                    .stream()
                    .filter(u -> u.getEmail().equals(userDto.getEmail()))
                    .allMatch(u -> u.getId().equals(userDto.getId()))) {
                user.setEmail(userDto.getEmail());
            } else {
                throw new UserAlreadyExistsException("Пользователь с E-mail=" + userDto.getEmail() + " уже существует!");
            }

        }

        UserDto updateUserDto = mapper.toUserDto(user);
        if (userDto.getPassword() != null) {
            updateUserDto.setPassword(userDto.getPassword());
        }
        keycloakUtils.updateKeycloakUser(updateUserDto);
        return mapper.toUserDto(repository.save(user));
    }

    @Override
    public void delete(Long userId) {
       String uuid = findUserById(userId).getUuid();
       keycloakUtils.deleteKeycloakUser(uuid);

        try {
            repository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException("Пользователь с ID=" + userId + " не найден!");
        }
    }

    @Override
    public User findUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID=" + id + " не найден!"));
    }
}
