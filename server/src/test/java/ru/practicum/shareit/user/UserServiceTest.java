package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private JpaUserRepository jpaUserRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUserById_whenUserFound_thenReturnedUser() {
        Long userId = 1L;
        User user = new User(userId, "Mike", "mike@test.com");
        UserDto expectedUserDto = UserMapper.toUserDto(user);

        when(jpaUserRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto actualUserDto = userService.getUserById(userId);

        assertEquals(expectedUserDto, actualUserDto);
        verify(jpaUserRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_whenUserNotFound_thenThrowNotFoundException() {
        Long userId = 1L;

        when(jpaUserRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
        assertEquals("Пользователь не найден", thrown.getMessage());
        verify(jpaUserRepository, times(1)).findById(userId);
    }

    @Test
    void createUser_whenEmailDoesNotExist_thenUserShouldBeCreated() {
        UserDto userDto = new UserDto(1L, "Mike", "mike@test.com");
        User user = UserMapper.toUser(userDto);

        when(jpaUserRepository.findAll()).thenReturn(Collections.emptyList());
        when(jpaUserRepository.save(any(User.class))).thenReturn(user);

        UserDto createdUser = userService.createUser(userDto);

        assertEquals(userDto.getEmail(), createdUser.getEmail());
        assertEquals(userDto.getName(), createdUser.getName());
        verify(jpaUserRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_whenEmailExists_thenConflictExceptionShouldBeThrown() {
        UserDto userDto = new UserDto(1L, "Mike", "mike@test.com");
        User existingUser = new User(2L, "John", "mike@test.com");

        when(jpaUserRepository.findAll()).thenReturn(Collections.singletonList(existingUser));

        ConflictException thrown = assertThrows(ConflictException.class, () -> userService.createUser(userDto));
        assertEquals("Существующий емайл", thrown.getMessage());

        verify(jpaUserRepository, never()).save(any());
    }

    @Test
    void updateUser_whenUserNotFound_thenNotFoundExceptionShouldBeThrown() {
        Long userId = 1L;
        UserDto userDto = new UserDto(userId, "Mike", "mike@test.com");

        when(jpaUserRepository.getById(userId)).thenThrow(new NotFoundException("Пользователь не найден"));

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> userService.updateUser(userId, userDto));
        assertEquals("Пользователь не найден", thrown.getMessage());

        verify(jpaUserRepository, never()).save(any());
    }

    @Test
    void updateUser_whenEmailExists_thenConflictExceptionShouldBeThrown() {
        Long userId = 1L;
        UserDto userDto = new UserDto(userId, "Mike", "mike@test.com");
        User existingUser = new User(2L, "John", "mike@test.com");
        User userToUpdate = new User(userId, "Old Name", "oldemail@test.com");

        when(jpaUserRepository.getById(userId)).thenReturn(userToUpdate);
        when(jpaUserRepository.findAll()).thenReturn(Arrays.asList(existingUser, userToUpdate));

        ConflictException thrown = assertThrows(ConflictException.class, () -> userService.updateUser(userId, userDto));
        assertEquals("Существующий емайл", thrown.getMessage());

        verify(jpaUserRepository, never()).save(any());
    }

    @Test
    void updateUser_whenValidUser_thenUserShouldBeUpdated() {
        Long userId = 1L;
        UserDto userDto = new UserDto(userId, "Mike", "newemail@test.com");
        User existingUser = new User(userId, "Mike", "oldemail@test.com");

        when(jpaUserRepository.getById(userId)).thenReturn(existingUser);
        when(jpaUserRepository.findAll()).thenReturn(Collections.singletonList(existingUser));

        UserDto updatedUser = userService.updateUser(userId, userDto);

        assertEquals("Mike", updatedUser.getName());
        assertEquals("newemail@test.com", updatedUser.getEmail());
        verify(jpaUserRepository).save(existingUser);
        assertEquals("newemail@test.com", existingUser.getEmail());
    }

    @Test
    void deleteUserById_whenUserExists_thenUserShouldBeDeleted() {
        Long userId = 1L;
        User existingUser = new User(userId, "Mike", "mike@test.com");

        when(jpaUserRepository.getById(userId)).thenReturn(existingUser);
        UserDto deletedUserDto = userService.deleteUserById(userId);

        assertEquals(existingUser.getId(), deletedUserDto.getId());
        assertEquals(existingUser.getName(), deletedUserDto.getName());
        assertEquals(existingUser.getEmail(), deletedUserDto.getEmail());

        verify(jpaUserRepository).deleteById(userId);
    }

    @Test
    void deleteUserById_whenUserDoesNotExist_thenNotFoundExceptionShouldBeThrown() {
        Long userId = 1L;

        when(jpaUserRepository.getById(userId)).thenThrow(new NotFoundException("Пользователь не найден"));

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> userService.deleteUserById(userId));
        assertEquals("Пользователь не найден", thrown.getMessage());

        verify(jpaUserRepository, never()).deleteById(anyLong());
    }
}

