package ru.practicum.shareit.itemRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestListDto;
import ru.practicum.shareit.request.repository.JpaItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {

    @Mock
    private JpaItemRequestRepository itemRequestRepository;

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private JpaItemRepository jpaItemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void createItemRequest_whenUserExists_thenItemRequestIsCreated() {
        Long userId = 1L;
        User user = new User(userId, "Mike", "mike@test.com");

        String description = "Need a book";
        LocalDateTime createdTime = LocalDateTime.now();
        ItemRequestDto itemRequestDto = new ItemRequestDto(null, description, createdTime);

        ItemRequest expectedItemRequest = new ItemRequest();
        expectedItemRequest.setDescription(itemRequestDto.getDescription());
        expectedItemRequest.setRequester(user);
        expectedItemRequest.setCreated(createdTime);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(expectedItemRequest);

        ItemRequest actualItemRequest = itemRequestService.createItemRequest(userId, itemRequestDto);

        assertEquals(expectedItemRequest.getDescription(), actualItemRequest.getDescription());
        assertEquals(expectedItemRequest.getRequester().getId(), actualItemRequest.getRequester().getId());
        assertNotNull(actualItemRequest.getCreated());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void getAllUsersRequests_whenUserExists_thenReturnRequests() {
        Long userId = 1L;
        User user = new User(userId, "Mike", "mike@test.com");

        ItemRequest request1 = new ItemRequest();
        request1.setDescription("Request 1");

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("Request 2");

        List<ItemRequest> requests = List.of(request1, request2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequester(user)).thenReturn(requests);

        List<ItemRequestDto> actualRequests = itemRequestService.getAllUsersRequests(userId);

        assertEquals(2, actualRequests.size());
        assertEquals("Request 1", actualRequests.get(0).getDescription());
        assertEquals("Request 2", actualRequests.get(1).getDescription());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).findByRequester(user);
    }

    @Test
    void getRequestById_whenRequestExists_thenReturnRequestDto() {
        Long requestId = 1L;
        ItemRequest request = new ItemRequest();
        request.setDescription("Request 1");

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        List<Item> items = List.of(new Item());
        when(jpaItemRepository.getItemsByRequestId(requestId)).thenReturn(items);

        ItemRequestListDto actualRequest = itemRequestService.getRequestById(requestId);

        assertEquals("Request 1", actualRequest.getDescription());
        assertEquals(1, actualRequest.getItems().size());

        verify(itemRequestRepository, times(1)).findById(requestId);
        verify(jpaItemRepository, times(1)).getItemsByRequestId(requestId);
    }

    @Test
    void getRequestById_whenRequestNotFound_thenThrowNotFoundException() {
        Long requestId = 1L;

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemRequestService.getRequestById(requestId);
        });

        assertEquals("Запрос не найден", exception.getMessage());

        verify(itemRequestRepository, times(1)).findById(requestId);
    }
}
