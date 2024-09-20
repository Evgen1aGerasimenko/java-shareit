package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCompleteDto;
import ru.practicum.shareit.item.dto.ItemDto;
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
    public ItemDto postItem(@RequestHeader(header) Long userId, @Valid @RequestBody ItemDto item) {
        return itemService.postItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(header) Long userId, @PathVariable Long itemId, @RequestBody ItemDto item) {
        return itemService.updateItem(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemCompleteDto getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemCompleteDto> getOwnersItems(@RequestHeader(header) Long userId) {
        return itemService.getOwnersItems(userId);
    }

    @GetMapping("/search")
    public List<Item> searchItemByNameOrDescription(@RequestParam("text") String text) {
        return itemService.searchItemByNameOrDescription(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(header) Long userId,
                                 @PathVariable Long itemId, @Valid @RequestBody CommentCreateDto commentCreateDto) {
        return itemService.postComment(userId, itemId, commentCreateDto);
    }
}
