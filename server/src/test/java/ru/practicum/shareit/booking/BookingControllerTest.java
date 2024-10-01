package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();
    private User user;
    private Item item;
    private Status status;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();

        user = new User(1L, "Mike", "mike@ya.ru");
        item = new Item();
        item.setId(1L);
        item.setName("item name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        status = Status.APPROVED;
    }

    @Test
    void createBooking_ShouldReturnCheckResponseStatus() throws Exception {
        Long userId = 1L;

        BookingDto createdBookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user, status);
        CreateBookingDto createBookingDto = new CreateBookingDto();

        when(bookingService.createBooking(eq(userId), any(CreateBookingDto.class))).thenReturn(createdBookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBookingDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingById_ShouldReturnBooking_AndCheckResponseStatus() throws Exception {
        Long bookingId = 2L;
        Long userId = 1L;

        BookingDto bookingDto = new BookingDto(bookingId, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                item, user, status);

        when(bookingService.getBookingById(eq(userId), eq(bookingId))).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @Test
    void bookingApproving_ShouldReturnApprovedBooking_AndCheckResponseStatus() throws Exception {
        Long userId = 1L;
        Long bookingId = 2L;
        boolean approved = true;

        BookingDto approvedBookingDto = new BookingDto(bookingId, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                item, user, Status.APPROVED);

        when(bookingService.bookingApproving(eq(userId), eq(bookingId), eq(approved))).thenReturn(approvedBookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk());

        verify(bookingService).bookingApproving(eq(userId), eq(bookingId), eq(approved));
    }

    @Test
    void getAllBookings_ShouldReturnBookings_AndCheckResponseStatus() throws Exception {
        Long userId = 1L;
        List<BookingDto> bookings = Collections.singletonList(new BookingDto(1L, LocalDateTime.now(),
                LocalDateTime.now().plusDays(1), item, user, Status.APPROVED));

        when(bookingService.getAllBookings(eq(userId), any(BookingState.class))).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk());

        verify(bookingService).getAllBookings(eq(userId), any(BookingState.class));
    }

    @Test
    void getAllBookingsByOwner_ShouldReturnBookingsByOwner_AndCheckResponseStatus() throws Exception {
        Long userId = 1L;
        List<BookingDto> bookings = Collections.singletonList(new BookingDto(1L, LocalDateTime.now(),
                LocalDateTime.now().plusDays(1), item, user, Status.APPROVED));

        when(bookingService.getAllBookingsByOwner(eq(userId), any(BookingState.class))).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk());

        verify(bookingService).getAllBookingsByOwner(eq(userId), any(BookingState.class));
    }
}
