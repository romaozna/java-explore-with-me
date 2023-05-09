package ru.practicum.ewm.comment.mapper;

import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getAuthor(),
                comment.getEvent(),
                comment.getText(),
                comment.getCreated());
    }

    public static Comment toComment(CommentDto commentDto, Event event, User user) {
        return Comment.builder()
                .id(commentDto.getId())
                .author(user)
                .event(event)
                .text(commentDto.getText())
                .created(commentDto.getCreated())
                .build();
    }
}
