package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments")
public class CommentPrivateController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@Positive @PathVariable Long userId,
                             @Positive @RequestParam Long eventId,
                             @Valid @RequestBody CommentDto commentDto) {

        return commentService.create(userId, eventId, commentDto);
    }

    @GetMapping
    public List<CommentDto> getByUser(@Positive @PathVariable Long userId,
                                      @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                      @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(from, size);
        return commentService.getAllByUserId(userId, pageable);
    }

    @GetMapping("/{commentId}")
    public CommentDto getById(@Positive @PathVariable Long userId,
                              @Positive @PathVariable Long commentId) {

        return commentService.getById(userId, commentId);
    }

    @GetMapping("/event/{eventId}")
    public List<CommentDto> getByEvent(@Positive @PathVariable Long userId,
                                       @Positive @PathVariable Long eventId,
                                       @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                       @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(from, size);
        return commentService.getAllByEventId(userId, eventId, pageable);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByUser(@Positive @PathVariable Long userId,
                             @Positive @PathVariable Long commentId) {
        commentService.deleteByUser(userId, commentId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto update(@Positive @PathVariable Long userId,
                             @Positive @PathVariable Long commentId,
                             @Positive @RequestParam Long eventId,
                             @Valid @RequestBody CommentDto commentDto) {
        return commentService.update(commentId, userId, eventId, commentDto);
    }
}
