package ru.practicum.ewm.event.service;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.location.dto.LocationDto;
import ru.practicum.ewm.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventDto create(
            NewEventDto newEventDto,
            LocationDto locationDto,
            UserDto userDto,
            CategoryDto categoryDto);

    List<EventDto> getEventsByUserId(Long userId, Integer from, Integer size);

    EventDto getEventByUserIdAndEventId(Long userId, Long eventId);

    EventDto updateEventByUserIdAndEventId(Long userId, Long eventId, UpdateEventDto updateEventDto);

    EventDto updateEventByEventId(Long eventId, UpdateEventDto updateEventDto);

    List<EventDto> getEvents(LocalDateTime rangeStart, LocalDateTime rangeEnd, List<Long> users,
                             List<EventState> states, List<Long> categories, Integer from, Integer size);

    List<EventDto> getPublicEvents(Integer from, Integer size, EventState state,
                                   String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                   LocalDateTime rangeEnd, SortVariant sort, Boolean onlyAvailable);

    EventDto getPublicEventById(Long eventId);

    void createNewHit(String ip, String url);
}
