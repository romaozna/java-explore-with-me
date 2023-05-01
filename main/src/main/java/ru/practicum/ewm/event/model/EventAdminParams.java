package ru.practicum.ewm.event.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.event.dto.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class EventAdminParams {
    private List<EventState> states;
    private List<Long> categories;
    private List<Long> users;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
}