package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCompleteDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto postItem(Long userId, ItemDto item);

    ItemDto updateItem(Long userId, Long itemId, ItemDto item);

    ItemCompleteDto getItemById(Long itemId);

    List<ItemCompleteDto> getOwnersItems(Long userId);

    List<Item> searchItemByNameOrDescription(String text);

    CommentDto postComment(Long userId, Long itemId, CommentCreateDto commentCreateDto);
}
