package ru.practicum.shareit.itemRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestListDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(itemRequestController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void createItemRequest_ShouldReturnCreatedItemRequest_AndCheckResponseStatus() throws Exception {
        Long userId = 1L;

        User user = new User(userId, "Username", "user@example.com");
        ItemRequest expected = new ItemRequest(1L, "Request description", user, LocalDateTime.now());

        when(itemRequestService.createItemRequest(eq(userId), any(ItemRequestDto.class))).thenReturn(expected);

        MvcResult result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"Request description\"}"))
                .andExpect(status().isOk())
                .andReturn();

        verify(itemRequestService).createItemRequest(eq(userId), any(ItemRequestDto.class));
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    void getAllUsersRequests_ShouldReturnListOfItemRequestDto_AndCheckResponseStatus() throws Exception {
        Long userId = 1L;
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Request description", null);
        List<ItemRequestDto> expectedRequests = Collections.singletonList(itemRequestDto);

        when(itemRequestService.getAllUsersRequests(eq(userId))).thenReturn(expectedRequests);

        MvcResult result = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        verify(itemRequestService).getAllUsersRequests(eq(userId));

        String jsonResponse = result.getResponse().getContentAsString();
        String expectedJson = "[{\"id\":1,\"description\":\"Request description\",\"created\":null}]";
        assertEquals(expectedJson, jsonResponse);
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    void getAllRequests_ShouldReturnListOfItemRequestDto_AndCheckResponseStatus() throws Exception {
        ItemRequestDto itemRequestDto1 = new ItemRequestDto(1L, "First request description", null);
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(2L, "Second request description", null);
        List<ItemRequestDto> expectedRequests = Arrays.asList(itemRequestDto1, itemRequestDto2);

        when(itemRequestService.getAllRequests()).thenReturn(expectedRequests);

        MvcResult result = mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        verify(itemRequestService).getAllRequests();

        String jsonResponse = result.getResponse().getContentAsString();
        String expectedJson = "[{\"id\":1,\"description\":\"First request description\",\"created\":null}," +
                "{\"id\":2,\"description\":\"Second request description\",\"created\":null}]";
        assertEquals(expectedJson, jsonResponse);
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    void getRequestById_ShouldReturnCheckResponseStatus() throws Exception {
        Long requestId = 1L;
        when(itemRequestService.getRequestById(eq(requestId))).thenReturn(new ItemRequestListDto(requestId,
                "Request description", LocalDateTime.now(), new ArrayList<>()));

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(itemRequestService).getRequestById(eq(requestId));
    }
}
