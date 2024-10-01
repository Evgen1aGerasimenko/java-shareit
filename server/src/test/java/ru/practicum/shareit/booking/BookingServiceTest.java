package ru.practicum.shareit.booking;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private JpaBookingRepository bookingRepository;

    @Mock
    private UserService userService;

    @Mock
    private JpaItemRepository jpaItemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private UserDto userDto;
    private CreateBookingDto createBookingDto;
    private Item item;
    private User user;
    private User owner;
    private User booker;
    private Booking booking;
    private Booking approvedBooking;
    private Booking notapprovedBooking;
    private Booking lastBooking;
    private Booking nextBooking;
    private Booking pastBooking;
    private Booking futureBooking;

    @BeforeEach
    void setUp() {
        Long userId = 1L;
        userDto = new UserDto(userId, "Mike", "mike@test.com");
        createBookingDto = new CreateBookingDto();
        createBookingDto.setItemId(2L);

        user = new User(userId, "Mike", "mike@ya.ru");
        item = new Item();
        item.setId(2L);
        item.setName("item name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        owner = new User(1L, "Owner", "owner@test.com");
        booker = new User(2L, "Booker", "booker@test.com");
        booking = new Booking();
        booking.setId(3L);
        booking.setItem(item);
        booking.setBooker(owner);
        booking.setStatus(Status.WAITING);

        approvedBooking = new Booking();
        approvedBooking.setId(4L);
        approvedBooking.setItem(item);
        approvedBooking.setBooker(owner);
        approvedBooking.setStatus(Status.APPROVED);
        approvedBooking.setStart(LocalDateTime.now().plusDays(1));
        approvedBooking.setEnd(LocalDateTime.now().plusDays(2));

        notapprovedBooking = new Booking();
        notapprovedBooking.setId(5L);
        notapprovedBooking.setItem(item);
        notapprovedBooking.setBooker(owner);
        notapprovedBooking.setStatus(Status.REJECTED);
        notapprovedBooking.setStart(LocalDateTime.now().plusDays(1));
        notapprovedBooking.setEnd(LocalDateTime.now().plusDays(2));


        booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(1L));
        booking.setEnd(LocalDateTime.now().plusDays(1L));

        lastBooking = new Booking();
        lastBooking.setId(2L);
        lastBooking.setItem(item);
        lastBooking.setBooker(user);
        lastBooking.setStatus(Status.APPROVED);
        lastBooking.setStart(LocalDateTime.now().minusDays(2L));
        lastBooking.setEnd(LocalDateTime.now().minusDays(1L));

        pastBooking = new Booking();
        pastBooking.setId(3L);
        pastBooking.setItem(item);
        pastBooking.setBooker(user);
        pastBooking.setStatus(Status.APPROVED);
        pastBooking.setStart(LocalDateTime.now().minusDays(1L));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1L));

        nextBooking = new Booking();
        nextBooking.setId(4L);
        nextBooking.setItem(item);
        nextBooking.setBooker(user);
        nextBooking.setStatus(Status.APPROVED);
        nextBooking.setStart(LocalDateTime.now().plusDays(1L));
        nextBooking.setEnd(LocalDateTime.now().plusDays(2L));

        futureBooking = new Booking();
        futureBooking.setId(5L);
        futureBooking.setItem(item);
        futureBooking.setBooker(user);
        futureBooking.setStatus(Status.APPROVED);
        futureBooking.setStart(LocalDateTime.now().plusDays(1L));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2L));
    }

    @Test
    void createBooking_whenUserExistsAndItemAvailable_thenReturnBookingDto() {
        Long userId = userDto.getId();

        when(userService.getUserById(userId)).thenReturn(userDto);
        when(jpaItemRepository.findById(createBookingDto.getItemId())).thenReturn(Optional.of(item));

        Booking booking = BookingMapper.toBooking(createBookingDto, ItemMapper.toItemDto(item), userDto, Status.WAITING);
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto actualBookingDto = bookingService.createBooking(userId, createBookingDto);

        assertNotNull(actualBookingDto);
        verify(userService, atLeast(1)).getUserById(userId);
        verify(jpaItemRepository, times(1)).findById(createBookingDto.getItemId());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void createBooking_whenUserNotFound_thenThrowException() {
        Long userId = 1L;
        when(userService.getUserById(userId)).thenThrow(new NotFoundException("User not found"));

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(userId, createBookingDto));
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void getBookingById_whenBookingExistsAndIsOwner_thenReturnBookingDto() {
        Long userId = 1L;
        when(bookingRepository.getById(3L)).thenReturn(booking);

        BookingDto actualBookingDto = bookingService.getBookingById(userId, 3L);

        assertNotNull(actualBookingDto);
        assertEquals(3L, actualBookingDto.getId());
        verify(bookingRepository, times(1)).getById(3L);
    }

    @Test
    void getBookingById_whenBookingDoesNotExist_thenThrowNotFoundException() {
        Long userId = 1L;
        when(bookingRepository.getById(3L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(userId, 3L));
    }

    @Test
    void getBookingById_whenUserIsNotOwnerOrBooker_thenThrowForbiddenException() {
        Long userId = 3L;
        when(bookingRepository.getById(3L)).thenReturn(booking);

        assertThrows(ForbiddenException.class, () -> bookingService.getBookingById(userId, 3L));
    }

    @Test
    void bookingApproving_whenUserIsOwnerAndApproved_thenStatusUpdatedToApproved() {
        Long userId = owner.getId();
        booking.setStatus(Status.WAITING);

        when(bookingRepository.getById(booking.getId())).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingDto result = bookingService.bookingApproving(userId, booking.getId(), true);

        assertNotNull(result);
        assertEquals(Status.APPROVED, result.getStatus());
    }

    @Test
    void bookingApproving_whenUserIsOwnerAndNotApproved_thenStatusUpdatedToRejected() {
        Long userId = owner.getId();
        booking.setStatus(Status.WAITING);

        when(bookingRepository.getById(booking.getId())).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking savedBooking = invocation.getArgument(0);
            return savedBooking;
        });
        BookingDto result = bookingService.bookingApproving(userId, booking.getId(), false);
        assertNotNull(result);
        assertEquals(Status.REJECTED, result.getStatus());
    }

    @Test
    void bookingApproving_whenBookingDoesNotExist_thenThrowNotFoundException() {
        Long nonExistentBookingId = 999L;
        Long userId = owner.getId();

        when(bookingRepository.getById(nonExistentBookingId)).thenThrow(new NotFoundException("Booking not found"));

        assertThrows(NotFoundException.class, () -> bookingService.bookingApproving(userId, nonExistentBookingId, true));
    }

    @Test
    void bookingApproving_whenUserIsNotOwner_thenThrowForbiddenException() {
        Long userId = 2L;
        when(bookingRepository.getById(3L)).thenReturn(booking);

        assertThrows(ForbiddenException.class, () -> bookingService.bookingApproving(userId, 3L, true));
    }

    @Test
    void getAllBookings_whenUserExistsAndStateIsAll_thenReturnAllBookings() {
        Long userId = user.getId();
        when(userService.getUserById(userId)).thenReturn(new UserDto(userId, user.getName(), user.getEmail()));
        when(bookingRepository.findAllBookingsByBooker_IdOrderByStartDesc(userId))
                .thenReturn(List.of(approvedBooking, notapprovedBooking));

        List<BookingDto> actualBookings = bookingService.getAllBookings(userId, BookingState.ALL);

        assertEquals(2, actualBookings.size());
        assertTrue(actualBookings.stream().anyMatch(b -> b.getId().equals(approvedBooking.getId())));
        assertTrue(actualBookings.stream().anyMatch(b -> b.getId().equals(notapprovedBooking.getId())));
    }

    @Test
    void getAllBookings_whenUserExistsAndStateIsApproved_thenReturnApprovedBookings() {
        Long userId = user.getId();
        when(userService.getUserById(userId)).thenReturn(new UserDto(userId, user.getName(), user.getEmail()));
        when(bookingRepository.findAllBookingsByBooker_IdAndStatus(userId, Status.APPROVED))
                .thenReturn(List.of(approvedBooking));

        List<BookingDto> actualBookings = bookingService.getAllBookings(userId, BookingState.CURRENT);

        assertEquals(1, actualBookings.size());
        assertEquals(approvedBooking.getId(), actualBookings.get(0).getId());
    }

    @Test
    void getAllBookings_whenUserExistsAndStateIsRejected_thenReturnRejectedBookings() {
        Long userId = user.getId();
        when(userService.getUserById(userId)).thenReturn(new UserDto(userId, user.getName(), user.getEmail()));
        when(bookingRepository.findBookingsByBooker_IdAndStatus(userId, Status.REJECTED))
                .thenReturn(List.of(approvedBooking));

        List<BookingDto> actualBookings = bookingService.getAllBookings(userId, BookingState.REJECTED);

        assertEquals(1, actualBookings.size());
        assertEquals(approvedBooking.getId(), actualBookings.get(0).getId());
    }

    @Test
    void getAllBookings_ShouldReturnAllBookings_WhenStateIsAll() {
        Long userId = user.getId();
        when(bookingRepository.findAllBookingsByBooker_IdOrderByStartDesc(userId))
                .thenReturn(Arrays.asList(nextBooking));
        when(userService.getUserById(userId)).thenReturn(userDto);

        List<BookingDto> result = bookingService.getAllBookings(userId, BookingState.ALL);

        assertEquals(1, result.size());
        verify(bookingRepository).findAllBookingsByBooker_IdOrderByStartDesc(userId);
    }

    @Test
    void getAllBookings_ShouldReturnCurrentBookings_WhenStateIsCurrent() {
        Long userId = user.getId();
        when(bookingRepository.findAllBookingsByBooker_IdAndStatus(userId, Status.APPROVED))
                .thenReturn(Collections.singletonList(approvedBooking));
        when(userService.getUserById(userId)).thenReturn(userDto);

        List<BookingDto> result = bookingService.getAllBookings(userId, BookingState.CURRENT);

        assertEquals(1, result.size());
        verify(bookingRepository).findAllBookingsByBooker_IdAndStatus(userId, Status.APPROVED);
    }

    @Test
    void getAllBookings_ShouldReturnPastBookings_WhenStateIsPast() {
        Long userId = user.getId();
        when(bookingRepository.findAllBookingsByBooker_IdAndEndIsBeforeOrderByStartDesc(userId,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)))
                .thenReturn(Collections.singletonList(pastBooking));
        when(userService.getUserById(userId)).thenReturn(userDto);

        List<BookingDto> result = bookingService.getAllBookings(userId, BookingState.PAST);

        assertEquals(1, result.size());
        verify(bookingRepository).findAllBookingsByBooker_IdAndEndIsBeforeOrderByStartDesc(userId,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    void getAllBookings_ShouldReturnFutureBookings_WhenStateIsFuture() {
        Long userId = user.getId();
        when(bookingRepository.findAllBookingsByBooker_IdAndStartIsAfterOrderByStartDesc(userId,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)))
                .thenReturn(Collections.singletonList(futureBooking));
        when(userService.getUserById(userId)).thenReturn(userDto);

        List<BookingDto> result = bookingService.getAllBookings(userId, BookingState.FUTURE);

        assertEquals(1, result.size());
        verify(bookingRepository).findAllBookingsByBooker_IdAndStartIsAfterOrderByStartDesc(userId,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    void getAllBookings_ShouldReturnWaitingBookings_WhenStateIsWaiting() {
        Long userId = user.getId();
        when(bookingRepository.findBookingsByBooker_IdAndStatus(userId, Status.WAITING))
                .thenReturn(Collections.emptyList());
        when(userService.getUserById(userId)).thenReturn(userDto);

        List<BookingDto> result = bookingService.getAllBookings(userId, BookingState.WAITING);

        assertTrue(result.isEmpty());
        verify(bookingRepository).findBookingsByBooker_IdAndStatus(userId, Status.WAITING);
    }

    @Test
    void getAllBookings_ShouldReturnRejectedBookings_WhenStateIsRejected() {
        Long userId = user.getId();
        when(bookingRepository.findBookingsByBooker_IdAndStatus(userId, Status.REJECTED))
                .thenReturn(Collections.emptyList());
        when(userService.getUserById(userId)).thenReturn(userDto);

        List<BookingDto> result = bookingService.getAllBookings(userId, BookingState.REJECTED);

        assertTrue(result.isEmpty());
        verify(bookingRepository).findBookingsByBooker_IdAndStatus(userId, Status.REJECTED);
    }


    @Test
    void getAllBookingsByOwner_whenUserExistsAndStateIsAll_thenReturnAllBookings() {
        Long userId = user.getId();
        when(userService.getUserById(userId)).thenReturn(new UserDto(userId, user.getName(), user.getEmail()));
        when(bookingRepository.findAllBookingsByItemOwnerOrderByStartDesc(userId))
                .thenReturn(List.of(approvedBooking, notapprovedBooking));

        List<BookingDto> actualBookings = bookingService.getAllBookingsByOwner(userId, BookingState.ALL);

        assertEquals(2, actualBookings.size());
        assertTrue(actualBookings.stream().anyMatch(b -> b.getId().equals(approvedBooking.getId())));
        assertTrue(actualBookings.stream().anyMatch(b -> b.getId().equals(notapprovedBooking.getId())));
    }

    @Test
    void getAllBookingsByOwner_whenUserExistsAndStateIsApproved_thenReturnApprovedBookings() {
        Long userId = user.getId();
        when(userService.getUserById(userId)).thenReturn(new UserDto(userId, user.getName(), user.getEmail()));
        when(bookingRepository.findAllBookingsByItemOwnerAndStatus(userId, Status.APPROVED))
                .thenReturn(List.of(approvedBooking));

        List<BookingDto> actualBookings = bookingService.getAllBookingsByOwner(userId, BookingState.CURRENT);

        assertEquals(1, actualBookings.size());
        assertEquals(approvedBooking.getId(), actualBookings.get(0).getId());
    }

    @Test
    void getAllBookingsByOwner_whenUserExistsAndStateIsRejected_thenReturnRejectedBookings() {
        Long userId = user.getId();
        when(userService.getUserById(userId)).thenReturn(new UserDto(userId, user.getName(), user.getEmail()));
        when(bookingRepository.findBookingsByItemOwnerAndStatus(userId, Status.REJECTED))
                .thenReturn(List.of(notapprovedBooking));

        List<BookingDto> actualBookings = bookingService.getAllBookingsByOwner(userId, BookingState.REJECTED);

        assertEquals(1, actualBookings.size());
        assertEquals(notapprovedBooking.getId(), actualBookings.get(0).getId());
    }

    @Test
    void getAllBookingsByOwner_whenUserExistsAndStateIsPast_thenReturnPastBookings() {
        Long userId = user.getId();
        when(userService.getUserById(userId)).thenReturn(new UserDto(userId, user.getName(), user.getEmail()));
        when(bookingRepository.findAllBookingsByItemOwnerAndEndIsBeforeOrderByStartDesc(
                userId, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)))
                .thenReturn(List.of(notapprovedBooking));

        List<BookingDto> actualBookings = bookingService.getAllBookingsByOwner(userId, BookingState.PAST);

        assertEquals(1, actualBookings.size());
        assertEquals(notapprovedBooking.getId(), actualBookings.get(0).getId());
    }

    @Test
    void getAllBookingsByOwner_whenUserExistsAndStateIsFuture_thenReturnFutureBookings() {
        Long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(new UserDto(userId, user.getName(), user.getEmail()));
        when(bookingRepository.findAllBookingsByItemOwnerAndStartIsAfterOrderByStartDesc(
                userId, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)))
                .thenReturn(List.of(approvedBooking));

        List<BookingDto> actualBookings = bookingService.getAllBookingsByOwner(userId, BookingState.FUTURE);

        assertEquals(1, actualBookings.size());
        assertEquals(approvedBooking.getId(), actualBookings.get(0).getId());
    }

    @Test
    void getAllBookingsByOwner_whenUserExistsAndStateIsWaiting_thenReturnWaitingBookings() {

        when(userService.getUserById(1L)).thenReturn(new UserDto(1L, user.getName(), user.getEmail()));
        when(bookingRepository.findBookingsByItemOwnerAndStatus(1L, Status.WAITING))
                .thenReturn(List.of(booking));

        List<BookingDto> actualBookings = bookingService.getAllBookingsByOwner(1L, BookingState.WAITING);

        assertEquals(1, actualBookings.size());
        assertEquals(booking.getId(), actualBookings.get(0).getId());
    }
}



