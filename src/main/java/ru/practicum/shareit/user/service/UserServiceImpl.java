package ru.practicum.shareit.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final JpaUserRepository jpaUserRepository;

    @Transactional
    @Override
    public UserDto createUser(UserDto user) {
        if (checkEmail(user.getEmail()) != null) {
            throw new ConflictException("Существующий емайл");
        }
        User dtoToModelOfUser = UserMapper.toUser(user);
        return UserMapper.toUserDto(jpaUserRepository.save(dtoToModelOfUser));
    }

    @Transactional
    @Override
    public UserDto updateUser(Long id, UserDto user) {
        checkUser(id);
        if (checkEmail(user.getEmail()) != null) {
            throw new ConflictException("Существующий емайл");
        }
        User updatedUser = jpaUserRepository.getById(id);
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
        }
        jpaUserRepository.save(updatedUser);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = jpaUserRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto deleteUserById(Long id) {
        checkUser(id);
        User user = jpaUserRepository.getById(id);
        jpaUserRepository.deleteById(id);
        return UserMapper.toUserDto(user);
    }

    private User checkEmail(String email) {
        return jpaUserRepository.findAll().stream()
                .filter(userDto -> userDto.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    private void checkUser(Long id) {
        if (jpaUserRepository.getById(id) == null) {
            throw new NotFoundException("Пользователь не найден");
        }
    }
}
