package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;
import ru.practicum.shareit.user.service.UserService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceIntegrationTest {
    private final UserService userService;
    private final JpaUserRepository userRepository;

    private Long userId;

    @BeforeEach
    void setUp() {
        UserDto user = new UserDto();
        user.setName("mike");
        user.setEmail("mike@ya.ru");
        user = userService.createUser(user);
        userId = user.getId();
    }

    @Test
    void updateUser_ShouldUpdateUser_WhenDataIsValid() {
        UserDto updatedUser = new UserDto();
        updatedUser.setName("mike_updated");
        updatedUser.setEmail("mike_updated@ya.ru");

        UserDto result = userService.updateUser(userId, updatedUser);

        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getName()).isEqualTo("mike_updated");
        assertThat(result.getEmail()).isEqualTo("mike_updated@ya.ru");

        User userFromDb = userRepository.getById(userId);
        assertThat(userFromDb.getName()).isEqualTo("mike_updated");
        assertThat(userFromDb.getEmail()).isEqualTo("mike_updated@ya.ru");
    }

    @Test
    void updateUser_ShouldThrowConflictException_WhenEmailIsAlreadyExists() {
        UserDto anotherUser = new UserDto();
        anotherUser.setName("john");
        anotherUser.setEmail("mike_updated@ya.ru");
        userService.createUser(anotherUser);

        UserDto updatedUser = new UserDto();
        updatedUser.setEmail("mike_updated@ya.ru");

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            userService.updateUser(userId, updatedUser);
        });

        assertThat(exception.getMessage()).isEqualTo("Существующий емайл");
    }
}