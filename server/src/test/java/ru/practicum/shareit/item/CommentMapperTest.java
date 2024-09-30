package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    private final CommentMapper commentMapper = new CommentMapper();

    @Test
    void toCommentDto_ShouldConvertCommentToCommentDto() {
        User user = new User();
        user.setName("UserName");
        Item item = new Item();
        item.setId(1L);
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Great item!");
        comment.setUser(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        CommentDto commentDto = commentMapper.toCommentDto(comment);

        assertNotNull(commentDto);
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(user.getName(), commentDto.getAuthorName());
        assertEquals(item.getId(), commentDto.getItemId());
        assertEquals(comment.getCreated(), commentDto.getCreated());
    }

    @Test
    void toComment_ShouldConvertCommentDtoToComment() {
        User user = new User();
        user.setId(2L);
        user.setName("UserName");
        Item item = new Item();
        item.setId(1L);
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Great item!");
        commentDto.setCreated(LocalDateTime.now());

        Comment comment = commentMapper.toComment(commentDto, item, user);

        assertNotNull(comment);
        assertEquals(commentDto.getId(), comment.getId());
        assertEquals(commentDto.getText(), comment.getText());
        assertEquals(user, comment.getUser());
        assertEquals(item, comment.getItem());
        assertEquals(commentDto.getCreated(), comment.getCreated());
    }

    @Test
    void toComment_ShouldConvertCommentCreateDtoToComment() {
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("Amazing!");

        Comment comment = commentMapper.toComment(commentCreateDto);

        assertNotNull(comment);
        assertEquals(commentCreateDto.getText(), comment.getText());
        assertNull(comment.getUser());
        assertNull(comment.getItem());
        assertNull(comment.getCreated());
    }
}