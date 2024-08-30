package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item postItem(Long userId, Item item);

    Item updateItem(Long userId, Long itemId, Item item);

    Item getItemById(Long itemId);

    List<Item> getOwnersItems(Long userId);

    List<Item> searchItemByNameOrDescription(String text);
}
