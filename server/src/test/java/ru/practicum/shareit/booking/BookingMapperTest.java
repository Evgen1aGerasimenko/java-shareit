package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingMapperTest {

    @Test
    public void toBookingDto_ShouldMapBookingToBookingDto() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(new Item());
        booking.setBooker(new User());
        booking.setStatus(Status.APPROVED);

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getId()).isEqualTo(booking.getId());
        assertThat(bookingDto.getStart()).isEqualTo(booking.getStart());
        assertThat(bookingDto.getEnd()).isEqualTo(booking.getEnd());
        assertThat(bookingDto.getItem()).isEqualTo(booking.getItem());
        assertThat(bookingDto.getBooker()).isEqualTo(booking.getBooker());
        assertThat(bookingDto.getStatus()).isEqualTo(booking.getStatus());
    }

    @Test
    public void toBooking_ShouldMapBookingDtoToBooking() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItem(new Item());
        bookingDto.setBooker(new User());
        bookingDto.setStatus(Status.APPROVED);

        Booking booking = BookingMapper.toBooking(bookingDto);

        assertThat(booking).isNotNull();
        assertThat(booking.getId()).isEqualTo(bookingDto.getId());
        assertThat(booking.getStart()).isEqualTo(bookingDto.getStart());
        assertThat(booking.getEnd()).isEqualTo(bookingDto.getEnd());
        assertThat(booking.getItem()).isEqualTo(bookingDto.getItem());
        assertThat(booking.getBooker()).isEqualTo(bookingDto.getBooker());
        assertThat(booking.getStatus()).isEqualTo(bookingDto.getStatus());
    }
}