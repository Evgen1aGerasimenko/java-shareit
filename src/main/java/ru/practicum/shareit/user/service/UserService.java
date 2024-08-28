package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

public interface UserService {
    User createUser(User user);

    User updateUser(Long id, User user);

    User getUserById(Long id);

    User deleteUserById(Long id);
}
