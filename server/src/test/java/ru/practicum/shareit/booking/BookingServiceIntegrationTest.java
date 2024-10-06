package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceIntegrationTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final JpaBookingRepository bookingRepository;


    Long ownerId;
    Long bookerId;

    @BeforeEach
    void setUp() {
        UserDto owner = new UserDto();
        owner.setName("mike");
        owner.setEmail("mike@ya.ru");
        owner = userService.createUser(owner);
        ownerId = owner.getId();

        UserDto booker = new UserDto();
        booker.setName("mike");
        booker.setEmail("mike1@ya.ru");
        booker = userService.createUser(booker);
        bookerId = booker.getId();

        ItemDto item1 = new ItemDto();
        item1.setName("item");
        item1.setDescription("new");
        item1.setAvailable(true);

        ItemDto item2 = new ItemDto();
        item2.setName("item");
        item2.setDescription("new");
        item2.setAvailable(true);

        item1 = itemService.createItem(ownerId, item1);
        item2 = itemService.createItem(ownerId, item2);

        LocalDateTime now = LocalDateTime.now();
        CreateBookingDto booking1 = new CreateBookingDto();
        booking1.setStart(now.minusDays(1));
        booking1.setEnd(now.plusDays(4));
        booking1.setItemId(item1.getId());

        CreateBookingDto booking2 = new CreateBookingDto();
        booking2.setStart(now.minusDays(2));
        booking2.setEnd(now.minusDays(1));
        booking2.setItemId(item2.getId());

        CreateBookingDto booking3 = new CreateBookingDto();
        booking3.setStart(now.plusDays(1));
        booking3.setEnd(now.plusDays(4));
        booking3.setItemId(item1.getId());

        CreateBookingDto booking4 = new CreateBookingDto();
        booking4.setStart(now.minusDays(2));
        booking4.setEnd(now.minusDays(1));
        booking4.setItemId(item2.getId());

        BookingDto bookingDto1 = bookingService.createBooking(bookerId, booking1);
        BookingDto bookingDto2 = bookingService.createBooking(bookerId, booking2);
        bookingService.createBooking(bookerId, booking3);
        bookingService.createBooking(bookerId, booking4);
        bookingDto1.setStatus(Status.REJECTED);
        bookingDto2.setStatus(Status.APPROVED);
        bookingRepository.save(BookingMapper.toBooking(bookingDto1));
        bookingRepository.save(BookingMapper.toBooking(bookingDto2));
    }

    @Test
    void getAllBookingsByOwner_ShouldReturnAllBookings_WhenStateIsAll() {
        List<BookingDto> bookings = bookingService.getAllBookingsByOwner(ownerId, BookingState.ALL);
        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(4);
    }

    @Test
    void getAllBookingsByOwner_ShouldReturnCurrentBookings_WhenStateIsCurrent() {
        List<BookingDto> bookings = bookingService.getAllBookingsByOwner(ownerId, BookingState.CURRENT);
        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
    }

    @Test
    void getAllBookingsByOwner_ShouldReturnPastBookings_WhenStateIsPast() {
        List<BookingDto> bookings = bookingService.getAllBookingsByOwner(ownerId, BookingState.PAST);
        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(2);
    }

    @Test
    void getAllBookingsByOwner_ShouldReturnFutureBookings_WhenStateIsFuture() {
        List<BookingDto> bookings = bookingService.getAllBookingsByOwner(ownerId, BookingState.FUTURE);
        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
    }

    @Test
    void getAllBookingsByOwner_ShouldReturnWaitingBookings_WhenStateIsWaiting() {
        List<BookingDto> bookings = bookingService.getAllBookingsByOwner(ownerId, BookingState.WAITING);
        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(2);
    }

    @Test
    void getAllBookingsByOwner_ShouldReturnRejectedBookings_WhenStateIsRejected() {
        List<BookingDto> bookings = bookingService.getAllBookingsByOwner(ownerId, BookingState.REJECTED);
        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
    }
}