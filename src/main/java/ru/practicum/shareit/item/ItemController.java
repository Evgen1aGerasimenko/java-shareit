package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;
    private final String header = "X-Sharer-User-Id";

    @PostMapping
    public Item postItem(@RequestHeader(header) Long userId, @Valid @RequestBody Item item) {
        return itemService.postItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader(header) Long userId, @PathVariable Long itemId, @RequestBody Item item) {
        return itemService.updateItem(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public Item getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<Item> getOwnersItems(@RequestHeader(header) Long userId) {
        return itemService.getOwnersItems(userId);
    }

    @GetMapping("/search")
    public List<Item> searchItemByNameOrDescription(@RequestParam("text") String text) {
        return itemService.searchItemByNameOrDescription(text);
    }
}
