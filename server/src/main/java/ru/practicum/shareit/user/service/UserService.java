package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto createUser(UserDto user);

    UserDto updateUser(Long id, UserDto user);

    UserDto getUserById(Long id);

    UserDto deleteUserById(Long id);
}
