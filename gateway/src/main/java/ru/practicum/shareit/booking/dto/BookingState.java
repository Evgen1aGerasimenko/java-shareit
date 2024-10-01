package ru.practicum.shareit.booking.dto;


import java.util.Optional;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<BookingState> from(String stringState) {
        for (BookingState bookingState : values()) {
            if (bookingState.name().equalsIgnoreCase(stringState)) {
                return Optional.of(bookingState);
            }
        }
        return Optional.empty();
    }
}