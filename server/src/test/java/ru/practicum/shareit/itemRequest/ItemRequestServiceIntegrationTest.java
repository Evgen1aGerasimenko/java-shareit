package ru.practicum.shareit.itemRequest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceIntegrationTest {

    private final ItemRequestService itemRequestService;
    private final JpaUserRepository userRepository;

    private Long userId;

    @BeforeEach
    void setUp() {
        UserDto userDto = new UserDto();
        userDto.setName("mike");
        userDto.setEmail("mike@ya.ru");
        User user = userRepository.save(UserMapper.toUser(userDto));
        userId = user.getId();
    }

    @Test
    void createItemRequest_ShouldCreateItemRequest_WhenRequestIsValid() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Need a bike");

        ItemRequest createdRequest = itemRequestService.createItemRequest(userId, itemRequestDto);

        assertThat(createdRequest).isNotNull();
        assertThat(createdRequest.getId()).isNotNull();
        assertThat(createdRequest.getDescription()).isEqualTo("Need a bike");
        assertThat(createdRequest.getRequester().getId()).isEqualTo(userId);
        assertThat(createdRequest.getCreated()).isNotNull();
    }

    @Test
    void createItemRequest_ShouldThrowException_WhenUserDoesNotExist() {
        Long invalidUserId = 999L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Need a bike");

        assertThrows(NotFoundException.class, () -> {
            itemRequestService.createItemRequest(invalidUserId, itemRequestDto);
        });
    }
}
