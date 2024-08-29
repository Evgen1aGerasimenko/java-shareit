package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public Item postItem(Long userId, Item item) {
        userService.getUserById(userId);
        return itemRepository.postItem(userId, item);
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        userService.getUserById(userId);
        if (itemRepository.getItemById(itemId) == null) {
            throw new NotFoundException("Предмет не найден");
        }
        return itemRepository.updateItem(userId, itemId, item);
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemRepository.getItemById(itemId);
    }

    @Override
    public List<Item> getOwnersItems(Long userId) {
        return itemRepository.getOwnersItems(userId);
    }

    @Override
    public List<Item> searchItemByNameOrDescription(String text) {
        return itemRepository.searchItemByNameOrDescription(text);
    }
}
