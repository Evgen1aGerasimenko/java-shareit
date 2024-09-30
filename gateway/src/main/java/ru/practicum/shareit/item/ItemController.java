package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;
    private final String header = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(header) Long userId, @Valid @RequestBody ItemDto item) {
        return itemClient.createItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(header) Long userId, @PathVariable Long itemId, @RequestBody ItemDto item) {
        return itemClient.updateItem(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId) {
        return itemClient.getItemById(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnersItems(@RequestHeader(header) Long userId) {
        return itemClient.getOwnersItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemByNameOrDescription(@RequestParam("text") String text) {
        return itemClient.searchItemByNameOrDescription(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(header) Long userId,
                                             @PathVariable Long itemId, @Valid @RequestBody CommentCreateDto commentCreateDto) {
        return itemClient.postComment(userId, itemId, commentCreateDto);
    }
}
