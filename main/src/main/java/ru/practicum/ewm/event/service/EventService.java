package ru.practicum.ewm.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.EventAdminParams;
import ru.practicum.ewm.event.model.EventPublicParams;
import ru.practicum.ewm.location.dto.LocationDto;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface EventService {

    EventDto create(
            NewEventDto newEventDto,
            LocationDto locationDto,
            UserDto userDto,
            CategoryDto categoryDto);

    List<EventDto> getByUserId(Long userId, Pageable pageable);

    EventDto getByUserIdAndEventId(Long userId, Long eventId);

    EventDto updateByUserIdAndEventId(Long userId, Long eventId, UpdateEventDto updateEventDto);

    EventDto updateByEventId(Long eventId, UpdateEventDto updateEventDto);

    List<EventDto> getAll(EventAdminParams eventParams, Pageable pageable);

    List<EventDto> getPublicEvents(EventPublicParams eventParams, Pageable pageable);

    EventDto getPublicEventById(Long eventId);

    void createNewHit(String ip, String url);
}
