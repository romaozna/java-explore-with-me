package ru.practicum.ewm.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> getAllByAuthorIdAndEventId(Long authorId, Long eventId, Pageable pageable);
    List<Comment> getAllByAuthorId(Long authorId, Pageable pageable);
}
