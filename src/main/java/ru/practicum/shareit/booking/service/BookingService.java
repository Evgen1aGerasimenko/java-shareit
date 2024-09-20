package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.util.List;

public interface BookingService {
    BookingDto postBooking(Long userId, CreateBookingDto createBookingDto);

    BookingDto getBookingById(Long userId, Long bookingId);

    BookingDto bookingApproving(Long userId, Long bookingId, boolean approved);

    List<BookingDto> getAllBookings(Long userId, State state);

    List<BookingDto> getAllBookingsByOwner(Long userId, State state);
}
