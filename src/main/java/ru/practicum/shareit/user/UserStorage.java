package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    User create(User user);

    User update(User user);

    User delete(Long userId);

    List<User> getUsers();

    User getUserById(Long userId);
}
