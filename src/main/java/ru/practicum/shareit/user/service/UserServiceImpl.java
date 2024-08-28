package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Override
    public User createUser(User user) {
        if (checkEmail(user.getEmail()) != null) {
            throw new ConflictException("Существующий емайл");
        }
        return userRepository.createUser(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        if (checkEmail(user.getEmail()) != null) {
            throw new ConflictException("Существующий емайл");
        }
        return userRepository.updateUser(id, user);
    }

    @Override
    public User getUserById(Long id) {
        checkUser(id);
        return userRepository.getUserById(id);
    }

    @Override
    public User deleteUserById(Long id) {
        checkUser(id);
        return userRepository.deleteUserById(id);
    }

    private User checkEmail(String email) {
        return userRepository.getAllUsers().stream()
                .filter(userDto -> userDto.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    private void checkUser(Long id) {
        if (userRepository.getUserById(id) == null) {
            throw new NotFoundException("Пользователь не найден");
        }
    }
}
