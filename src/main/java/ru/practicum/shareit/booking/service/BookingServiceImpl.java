package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final JpaBookingRepository bookingRepository;
    private final UserService userService;
    private final JpaItemRepository jpaItemRepository;

    @Transactional
    @Override
    public BookingDto postBooking(Long userId, CreateBookingDto createBookingDto) {
        UserDto userDto = userService.getUserById(userId);
        long idOfItem = createBookingDto.getItemId();
        Item item = jpaItemRepository.findById(idOfItem)
                .orElseThrow(() -> new NotFoundException("Вещь с указанным ид не найдена"));

        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (!itemDto.getAvailable()) {
            throw new BadRequestException("Вещь не доступна для бронирования");
        }
        Booking booking = BookingMapper.toBooking(createBookingDto);
        booking.setBooker(UserMapper.toUser(userDto));
        booking.setItem(ItemMapper.toItem(itemDto));
        booking.setStatus(Status.WAITING);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        if (bookingRepository.getById(bookingId) == null) {
            throw new NotFoundException("Бронирование не найдено");
        }
        if (!bookingRepository.getById(bookingId).getItem().getOwner().getId().equals(userId) &&
                !bookingRepository.getById(bookingId).getBooker().getId().equals(userId)) {
            throw new ForbiddenException("Пользователь не является влвдельцем вещи");
        }
        return BookingMapper.toBookingDto(bookingRepository.getById(bookingId));
    }

    @Transactional
    @Override
    public BookingDto bookingApproving(Long userId, Long bookingId, boolean approved) {
        Booking booking = BookingMapper.toBooking(getBookingById(userId, bookingId));
        if (Boolean.TRUE.equals(approved)) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingDto> getAllBookings(Long userId, State state) {
        userService.getUserById(userId);
        List<Booking> bookings;

        switch (state) {
            case ALL -> bookings = bookingRepository.findAllBookingsByBooker_IdOrderByStartDesc(userId);
            case CURRENT -> bookings = bookingRepository.findAllBookingsByBooker_IdAndStatus(userId, Status.APPROVED);
            case PAST ->
                    bookings = bookingRepository.findAllBookingsByBooker_IdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case FUTURE ->
                    bookings = bookingRepository.findAllBookingsByBooker_IdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
            case WAITING -> bookings = bookingRepository.findBookingsByBooker_IdAndStatus(userId, Status.WAITING);
            case REJECTED -> bookings = bookingRepository.findBookingsByBooker_IdAndStatus(userId, Status.REJECTED);
            default -> throw new BadRequestException("Ошибка определения статуса");
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingsByOwner(Long userId, State state) {
        userService.getUserById(userId);
        List<Booking> bookings;

        switch (state) {
            case ALL -> bookings = bookingRepository.findAllBookingsByItemOwnerOrderByStartDesc(userId);
            case CURRENT -> bookings = bookingRepository.findAllBookingsByItemOwnerAndStatus(userId, Status.APPROVED);
            case PAST ->
                    bookings = bookingRepository.findAllBookingsByItemOwnerAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case FUTURE ->
                    bookings = bookingRepository.findAllBookingsByItemOwnerAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
            case WAITING -> bookings = bookingRepository.findBookingsByItemOwnerAndStatus(userId, Status.WAITING);
            case REJECTED -> bookings = bookingRepository.findBookingsByItemOwnerAndStatus(userId, Status.REJECTED);
            default -> throw new BadRequestException("Ошибка определения статуса");
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
