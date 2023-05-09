package ru.practicum.ewm.comment.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.comment.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto create(Long userId, Long eventId, CommentDto commentDto);

    CommentDto update(Long commentId, Long userId, Long eventId, CommentDto commentDto);

    CommentDto getById(Long userid, Long commentId);

    List<CommentDto> getAllByEventId(Long userId, Long eventId, Pageable pageable);

    List<CommentDto> getAllByUserId(Long userId, Pageable pageable);

    void deleteById(Long commentId);

    void deleteByUser(Long userId, Long commentId);
}
