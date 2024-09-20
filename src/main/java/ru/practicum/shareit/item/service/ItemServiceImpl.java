package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCompleteDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaCommentRepository;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final JpaItemRepository jpaItemRepository;
    private final UserService userService;
    private final JpaUserRepository jpaUserRepository;
    private final JpaCommentRepository jpaCommentRepository;
    private final JpaBookingRepository jpaBookingRepository;

    @Transactional
    @Override
    public ItemDto postItem(Long userId, ItemDto item) {
        jpaUserRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        User us = UserMapper.toUser(userService.getUserById(userId));
        Item dtoToModelOfItem = ItemMapper.toItem(item);
        dtoToModelOfItem.setOwner(us);
        return ItemMapper.toItemDto(jpaItemRepository.save(dtoToModelOfItem));
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto item) {
        Item updatedItem = jpaItemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с указанным ид не найдена"));
        if (!updatedItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Попытка обновления вещи другого пользователя");
        }
        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        return ItemMapper.toItemDto(jpaItemRepository.save(updatedItem));
    }

    @Override
    public ItemCompleteDto getItemById(Long itemId) {
        Item item = jpaItemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с указанным ид не найдена"));

        LocalDateTime now = LocalDateTime.now();

        Booking lastBookingEntity = jpaBookingRepository.findLastBookingByItem_Id(itemId, now);
        BookingDto lastBooking = (lastBookingEntity != null) ? BookingMapper.toBookingDto(lastBookingEntity) : null;

        Booking nextBookingEntity = jpaBookingRepository.findNextBookingByItem_Id(itemId, now);
        BookingDto nextBooking = (nextBookingEntity != null) ? BookingMapper.toBookingDto(nextBookingEntity) : null;

        List<CommentDto> commentDtos = jpaCommentRepository.findAllByItem_Id(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        return ItemMapper.toCompleteItem(item, lastBooking, nextBooking, commentDtos);

    }

    @Override
    public List<ItemCompleteDto> getOwnersItems(Long userId) {
        List<Item> items = jpaItemRepository.getItemsByOwnerId(userId);
        List<ItemCompleteDto> completeItems = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Item item : items) {
            Booking lastBookingEntity = jpaBookingRepository.findLastBookingByItem_Id(item.getId(), now);
            BookingDto lastBooking = (lastBookingEntity != null) ? BookingMapper.toBookingDto(lastBookingEntity) : null;

            Booking nextBookingEntity = jpaBookingRepository.findNextBookingByItem_Id(item.getId(), now);
            BookingDto nextBooking = (nextBookingEntity != null) ? BookingMapper.toBookingDto(nextBookingEntity) : null;

            List<Comment> comments = jpaCommentRepository.findAllByItem_Id(item.getId());
            List<CommentDto> commentDtos = comments.stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList());
            completeItems.add(ItemMapper.toCompleteItem(item, lastBooking, nextBooking, commentDtos));
        }
        return completeItems;
    }

    @Override
    public List<Item> searchItemByNameOrDescription(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return jpaItemRepository.searchItem(text);
    }

    @Override
    @Transactional
    public CommentDto postComment(Long userId, Long itemId, CommentCreateDto commentCreateDto) {
        User user = UserMapper.toUser(userService.getUserById(userId));
        Item item = jpaItemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с указанным ид не найдена"));
        List<Booking> bookings = jpaBookingRepository.findAllByBooker_IdAndItem_IdAndStatusAndEndBefore(userId, itemId, Status.APPROVED, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new BadRequestException("Вещь не была в бронировании у пользователея");
        }
        Comment comment = CommentMapper.toComment(commentCreateDto);
        comment.setUser(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(jpaCommentRepository.save(comment));
    }
}
