package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemCompleteDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.JpaCommentRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceIntegrationTest {

    private final ItemService itemService;
    private final UserService userService;
    private final JpaBookingRepository bookingRepository;
    private final JpaCommentRepository commentRepository;
    private final BookingService bookingService;

    private Long ownerId;
    private Long bookerId;
    private ItemDto item;
    private BookingDto booking1;


    @BeforeEach
    void setUp() {
        UserDto owner = new UserDto();
        owner.setName("mike");
        owner.setEmail("mike@ya.ru");
        owner = userService.createUser(owner);
        ownerId = owner.getId();

        UserDto booker = new UserDto();
        booker.setName("john");
        booker.setEmail("john@ya.ru");
        booker = userService.createUser(booker);
        bookerId = booker.getId();

        item = new ItemDto();
        item.setName("itemA");
        item.setDescription("Description of itemA");
        item.setAvailable(true);
        item = itemService.createItem(ownerId, item);

        LocalDateTime now = LocalDateTime.now();
        CreateBookingDto bookingDto1 = new CreateBookingDto();
        bookingDto1.setItemId(item.getId());
        bookingDto1.setStart(now.minusDays(2));
        bookingDto1.setEnd(now.plusDays(1));
        booking1 = bookingService.createBooking(bookerId, bookingDto1);
        booking1.setStatus(Status.APPROVED);
        bookingRepository.save(BookingMapper.toBooking(booking1));

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Great item!");
        comment.setItem(ItemMapper.toItem(item));
        comment.setUser(UserMapper.toUser(booker));
        comment.setCreated(now);
        commentRepository.save(comment);
    }

    @Test
    void getOwnersItems_ShouldReturnItemsWithBookingsAndComments() {
        List<ItemCompleteDto> items = itemService.getOwnersItems(ownerId);

        assertThat(items).isNotNull();
        assertThat(items).hasSize(1);

        ItemCompleteDto completeItem = items.get(0);

        assertThat(completeItem.getId()).isEqualTo(item.getId());
        assertThat(completeItem.getLastBooking()).isNotNull();
        assertThat(completeItem.getLastBooking().getId()).isEqualTo(booking1.getId());
        assertThat(completeItem.getComments()).isNotEmpty();
        assertThat(completeItem.getComments()).hasSize(1);
        assertThat(completeItem.getComments().get(0).getText()).isEqualTo("Great item!");
    }
}