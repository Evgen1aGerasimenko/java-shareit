package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCompleteDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
    }

    @Test
    void createItem_ShouldReturnCreatedItem_AndCheckResponseStatus() throws Exception {
        Long userId = 1L;
        ItemDto itemDto = new ItemDto(1L, "item_name", "item_description", true, 1L);

        when(itemService.createItem(eq(userId), any(ItemDto.class))).thenReturn(itemDto);

        MvcResult result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"item_name\", \"description\": \"item_description\"}"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        String responseBody = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ItemDto responseDto = objectMapper.readValue(responseBody, ItemDto.class);
        assertEquals(itemDto, responseDto);

        verify(itemService).createItem(eq(userId), any(ItemDto.class));
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem_AndCheckResponseStatus() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto updatedItemDto = new ItemDto(itemId, "item_name_updated", "item_description_updated",
                true, 1L);

        when(itemService.updateItem(eq(userId), eq(itemId), any(ItemDto.class))).thenReturn(updatedItemDto);

        MvcResult result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"item_name_updated\", \"description\": \"item_description_updated\"}"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ItemDto responseItemDto = objectMapper.readValue(jsonResponse, ItemDto.class);

        assertEquals(updatedItemDto, responseItemDto);

        verify(itemService).updateItem(eq(userId), eq(itemId), argThat(itemDto ->
                itemDto.getName().equals("item_name_updated") && itemDto.getDescription().equals("item_description_updated")));
    }

    @Test
    void getItemById_ShouldReturnStatus_WhenItemExists() throws Exception {
        Long itemId = 1L;

        Item item = new Item(itemId, "item_name", "item_description", true, null, null);

        User booker = new User(1L, "John Doe", "john@example.com");

        BookingDto lastBooking = new BookingDto(1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item, booker, Status.APPROVED);
        BookingDto nextBooking = new BookingDto(2L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, Status.WAITING);

        ItemCompleteDto itemCompleteDto = new ItemCompleteDto(
                itemId, "item_name", "item_description", true, lastBooking, nextBooking, new ArrayList<>()
        );

        when(itemService.getItemById(itemId)).thenReturn(itemCompleteDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemId))
                .andExpect(status().isOk());

        verify(itemService).getItemById(itemId);
    }

    @Test
    void getOwnersItems_ShouldReturnListOfItems_WhenItemsExist() throws Exception {
        Long userId = 1L;
        List<ItemCompleteDto> items = Arrays.asList(
                new ItemCompleteDto(1L, "item1", "description1",
                        true, null, null, null),
                new ItemCompleteDto(2L, "item2", "description2",
                        true, null, null, null)
        );

        when(itemService.getOwnersItems(userId)).thenReturn(items);

        MvcResult result = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        List<ItemCompleteDto> responseItems = Arrays.asList(objectMapper.readValue(responseBody, ItemCompleteDto[].class));
        assertEquals(items, responseItems);

        verify(itemService).getOwnersItems(userId);
    }

    @Test
    void searchItemByNameOrDescription_ShouldReturnStatus_WhenItemsMatchSearch() throws Exception {
        String searchText = "item";

        when(itemService.searchItemByNameOrDescription(searchText)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/items/search")
                        .param("text", searchText))
                .andExpect(status().isOk());

        verify(itemService).searchItemByNameOrDescription(searchText);
    }

    @Test
    void addComment_ShouldReturnStatus_WhenCommentIsAdded() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;

        CommentCreateDto commentCreateDto = new CommentCreateDto("Nice Item!");

        when(itemService.postComment(userId, itemId, commentCreateDto)).thenReturn(new CommentDto());

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\": \"Nice Item!\"}"))
                .andExpect(status().isOk());

        verify(itemService).postComment(eq(userId), eq(itemId), any(CommentCreateDto.class));
    }
}