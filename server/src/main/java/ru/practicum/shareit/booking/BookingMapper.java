package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(bookingDto.getItem());
        booking.setBooker(bookingDto.getBooker());
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }

    public static Booking toBooking(CreateBookingDto createBookingDto, ItemDto itemDto, UserDto userDto, Status waiting) {
        Booking booking = new Booking();
        booking.setStart(createBookingDto.getStart());
        booking.setEnd(createBookingDto.getEnd());
        booking.setItem(ItemMapper.toItem(itemDto));
        booking.setBooker(UserMapper.toUser(userDto));
        booking.setStatus(Status.WAITING);
        return booking;
    }
}
