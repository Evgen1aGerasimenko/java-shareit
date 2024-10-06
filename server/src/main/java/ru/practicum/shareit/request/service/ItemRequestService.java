package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestListDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequest createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllUsersRequests(Long userId);

    List<ItemRequestDto> getAllRequests();

    ItemRequestListDto getRequestById(Long requestId);
}
