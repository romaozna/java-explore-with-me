package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.OperationException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CommentDto create(Long userId, Long eventId, CommentDto commentDto) {
        User user = checkUser(userId);
        Event event = checkEvent(eventId);
        commentDto.setCreated(LocalDateTime.now());
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, event, user));
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto update(Long commentId, Long userId, Long eventId, CommentDto commentDto) {
        checkUser(userId);
        checkEvent(eventId);
        Comment comment = checkComment(commentId);
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new OperationException(String.format("User with id=%s is not the author of the comment", userId));
        }
        if (commentDto.getText() != null) {
            comment.setText(commentDto.getText());
        }
        comment.setCreated(LocalDateTime.now());
        comment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public CommentDto getById(Long userId, Long commentId) {
        checkUser(userId);
        Comment comment = checkComment(commentId);
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getAllByEventId(Long userId, Long eventId, Pageable pageable) {
        checkUser(userId);
        checkEvent(eventId);
        return commentRepository.getAllByAuthorIdAndEventId(userId, eventId, pageable).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAllByUserId(Long userId, Pageable pageable) {
        checkUser(userId);
        return commentRepository.getAllByAuthorId(userId, pageable).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(Long commentId) {
        checkComment(commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void deleteByUser(Long userId, Long commentId) {
        checkUser(userId);
        Comment comment = checkComment(commentId);
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new OperationException(String.format("User with id=%s is not the author of the comment", userId));
        }
        commentRepository.deleteById(commentId);
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException(String.format("User with id=%s was not found", userId));
        });
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Event with id=%s was not found", eventId));
        });
    }

    private Comment checkComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Comment with id=%s was not found", commentId));
        });
    }
}
