package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private long idGenerator = 0L;


    @Override
    public Item postItem(Long userId, Item item) {
        long id = ++idGenerator;
        item.setId(id);
        item.setOwner(userId);
        items.put(id, item);
        return item;
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        item.setId(itemId);
        item.setOwner(userId);
        item.setName(item.getName());
        item.setDescription(item.getDescription());
        item.setAvailable(item.getAvailable());
        return item;
    }

    @Override
    public Item getItemById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getOwnersItems(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner() != null && item.getOwner().longValue() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItemByNameOrDescription(String text) {
        List<Item> itemList = new ArrayList<>(items.values());
        List<Item> foundedItems = new ArrayList<>();
        for (Item item : itemList) {
            if ((item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                    item.getAvailable() &&
                    !text.isEmpty()) {
                foundedItems.add(item);
            }
        }
        return foundedItems;
    }
}