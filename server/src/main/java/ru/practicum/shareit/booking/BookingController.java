package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final String header = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto createBooking(@RequestHeader(header) Long userId, @RequestBody CreateBookingDto createBookingDto) {
        return bookingService.createBooking(userId, createBookingDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getUserById(@RequestHeader(header) Long userId, @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto bookingApproving(@RequestHeader(header) Long userId,
                                       @PathVariable Long bookingId,
                                       @RequestParam Boolean approved) {
        return bookingService.bookingApproving(userId, bookingId, approved);
    }

    @GetMapping
    public List<BookingDto> getAllBookings(@RequestHeader(header) Long userId,
                                           @RequestParam(required = false, defaultValue = "ALL") BookingState bookingState) {
        return bookingService.getAllBookings(userId, bookingState);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByOwner(@RequestHeader(header) Long userId,
                                                  @RequestParam(required = false, defaultValue = "ALL") BookingState bookingState) {
        return bookingService.getAllBookingsByOwner(userId, bookingState);
    }
}
