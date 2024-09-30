package ru.practicum.shareit.item;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCompleteDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaCommentRepository;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private JpaItemRepository jpaItemRepository;

    @Mock
    private UserService userService;

    @Mock
    private JpaUserRepository jpaUserRepository;

    @Mock
    private JpaCommentRepository jpaCommentRepository;

    @Mock
    private JpaBookingRepository jpaBookingRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private User user2;
    private UserDto userDto;
    private Item item;
    private ItemDto itemDto;
    private ItemCompleteDto itemCompleteDto;
    private ItemDto itemDtoUpdate;
    private Comment comment;
    private Booking booking;
    private Booking lastBooking;
    private Booking pastBooking;
    private Booking nextBooking;
    private Booking futureBooking;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("username");
        user.setEmail("email@email.com");

        user2 = new User();
        user2.setId(2L);
        user2.setName("username2");
        user2.setEmail("email2@email.com");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("username");
        userDto.setEmail("email@email.com");

        item = new Item();
        item.setId(1L);
        item.setName("item name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("item name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);

        itemCompleteDto = new ItemCompleteDto();
        itemCompleteDto.setId(1L);
        itemCompleteDto.setName("item name");
        itemCompleteDto.setDescription("description");
        itemCompleteDto.setAvailable(true);
        itemCompleteDto.setComments(Collections.emptyList());

        itemDtoUpdate = new ItemDto();
        itemDtoUpdate.setName("updated item name");
        itemDtoUpdate.setDescription("updated description");
        itemDtoUpdate.setAvailable(false);

        comment = new Comment();
        comment.setId(1L);
        comment.setText("comment");
        comment.setCreated(LocalDateTime.now());
        comment.setUser(user);
        comment.setItem(item);

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
    void createItem_ShouldReturnItemDto_WhenValidInput() {
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userService.getUserById(1L)).thenReturn(new UserDto(user.getId(), user.getName(), user.getEmail()));
        when(jpaItemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto createdItemDto = itemService.createItem(1L, itemDto);

        assertNotNull(createdItemDto);
        assertEquals(itemDto.getName(), createdItemDto.getName());
        assertEquals(itemDto.getDescription(), createdItemDto.getDescription());
        assertTrue(createdItemDto.getAvailable());
        verify(jpaUserRepository).findById(1L);
        verify(jpaItemRepository).save(any(Item.class));
    }

    @Test
    void createItem_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.createItem(1L, itemDto);
        });
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void updateItem_ShouldThrowNotFoundException_WhenItemNotFound() {
        when(jpaItemRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(1L, 1L, itemDto);
        });

        assertEquals("Вещь с указанным ид не найдена", exception.getMessage());
    }

    @Test
    void getItemById_ShouldReturnItemCompleteDto_WhenItemExists() {
        LocalDateTime fixedNow = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        when(jpaItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(jpaBookingRepository.findLastBookingByItem_Id(1L, fixedNow)).thenReturn(lastBooking);
        when(jpaBookingRepository.findNextBookingByItem_Id(1L, fixedNow)).thenReturn(nextBooking);
        when(jpaCommentRepository.findAllByItem_Id(1L)).thenReturn(List.of(comment));

        ItemCompleteDto itemCompleteDto = itemService.getItemById(1L);

        assertEquals(item.getId(), itemCompleteDto.getId());
        assertEquals(item.getName(), itemCompleteDto.getName());
        assertEquals(item.getDescription(), itemCompleteDto.getDescription());
        assertTrue(itemCompleteDto.getAvailable());
        assertNotNull(itemCompleteDto.getLastBooking());
        assertNotNull(itemCompleteDto.getNextBooking());
        assertFalse(itemCompleteDto.getComments().isEmpty());
    }

    @Test
    void getItemById_ShouldThrowNotFoundException_WhenItemNotFound() {
        when(jpaItemRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.getItemById(1L);
        });

        assertEquals("Вещь с указанным ид не найдена", exception.getMessage());
    }

    @Test
    void getOwnersItems_ShouldReturnListOfItemCompleteDto_WhenItemsExist() {
        LocalDateTime fixedNow = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        when(jpaItemRepository.getItemsByOwnerId(1L)).thenReturn(List.of(item));
        when(jpaBookingRepository.findLastBookingByItem_Id(1L, fixedNow)).thenReturn(lastBooking);
        when(jpaBookingRepository.findNextBookingByItem_Id(1L, fixedNow)).thenReturn(nextBooking);
        when(jpaCommentRepository.findAllByItem_Id(1L)).thenReturn(List.of(comment));

        List<ItemCompleteDto> result = itemService.getOwnersItems(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        ItemCompleteDto completeDto = result.get(0);
        assertEquals(item.getId(), completeDto.getId());
        assertEquals(item.getName(), completeDto.getName());
        assertNotNull(completeDto.getLastBooking());
        assertNotNull(completeDto.getNextBooking());
        assertFalse(completeDto.getComments().isEmpty());
    }

    @Test
    void getOwnersItems_ShouldReturnEmptyList_WhenNoItemsFound() {
        when(jpaItemRepository.getItemsByOwnerId(1L)).thenReturn(Collections.emptyList());

        List<ItemCompleteDto> result = itemService.getOwnersItems(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchItemByNameOrDescription_ShouldReturnListOfItems_WhenTextIsNotEmpty() {
        String searchText = "Item";
        when(jpaItemRepository.searchItem(searchText)).thenReturn(List.of(item));

        List<Item> result = itemService.searchItemByNameOrDescription(searchText);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void searchItemByNameOrDescription_ShouldReturnEmptyList_WhenTextIsEmpty() {
        List<Item> result = itemService.searchItemByNameOrDescription("");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void postComment_ShouldReturnCommentDto_WhenBookingExists() {
        LocalDateTime fixedNow = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        when(userService.getUserById(1L)).thenReturn(new UserDto(1l, "name", "T@YA.RU"));
        when(jpaItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(jpaBookingRepository.findAllByBooker_IdAndItem_IdAndStatusAndEndBefore(
                1L, 1L, Status.APPROVED, fixedNow))
                .thenReturn(List.of(booking));
        when(jpaCommentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CommentCreateDto commentCreateDto = new CommentCreateDto("new comment");

        CommentDto result = itemService.postComment(1L, 1L, commentCreateDto);

        assertNotNull(result);
        assertEquals(commentCreateDto.getText(), result.getText());
    }
}

