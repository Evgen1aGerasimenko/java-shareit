package ru.practicum.shareit.user;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void createUser_ShouldReturnCreatedUser_AndCheckResponseStatusss() throws Exception {
        UserDto userDto = new UserDto(1L, "mike", "mike@ya.ru");

        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

        MvcResult result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"mike\", \"email\": \"mike@ya.ru\"}"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        String responseBody = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        UserDto responseDto = objectMapper.readValue(responseBody, UserDto.class);
        assertEquals(userDto, responseDto);

        verify(userService).createUser(any(UserDto.class));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser_AndCheckResponseStatus() throws Exception {
        Long userId = 1L;
        UserDto updatedUserDto = new UserDto(userId, "mike_updated", "mike_updated@ya.ru");

        when(userService.updateUser(eq(userId), any(UserDto.class))).thenReturn(updatedUserDto);

        MvcResult result = mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"mike_updated\", \"email\": \"mike_updated@ya.ru\"}"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        UserDto responseUserDto = objectMapper.readValue(jsonResponse, UserDto.class);

        assertEquals(updatedUserDto, responseUserDto);

        verify(userService).updateUser(eq(userId), argThat(userDto ->
                userDto.getName().equals("mike_updated") && userDto.getEmail().equals("mike_updated@ya.ru")));
    }

    @Test
    void getUserById_ShouldReturnUser_AndCheckResponseStatus() throws Exception {
        Long userId = 1L;
        UserDto userDto = new UserDto(userId, "mike", "mike@ya.ru");

        when(userService.getUserById(userId)).thenReturn(userDto);

        MvcResult result = mockMvc.perform(get("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        UserDto responseUserDto = objectMapper.readValue(jsonResponse, UserDto.class);

        assertEquals(userDto, responseUserDto);

        verify(userService).getUserById(eq(userId));
    }

    @Test
    void deleteUserById_ShouldReturnDeletedUser_AndCheckResponseStatus() throws Exception {
        Long userId = 1L;
        UserDto userDto = new UserDto(userId, "mike", "mike@ya.ru");

        when(userService.deleteUserById(userId)).thenReturn(userDto);

        MvcResult result = mockMvc.perform(delete("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        UserDto responseUserDto = objectMapper.readValue(jsonResponse, UserDto.class);

        assertEquals(userDto, responseUserDto);

        verify(userService).deleteUserById(eq(userId));
    }
}

