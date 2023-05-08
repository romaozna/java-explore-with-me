package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventDto;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.location.dto.LocationDto;
import ru.practicum.ewm.location.dto.NewLocationDto;
import ru.practicum.ewm.location.service.LocationService;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventPrivateController {

    private final EventService eventService;

    private final LocationService locationService;

    private final UserService userService;

    private final CategoryService categoryService;

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto create(@PathVariable Long userId,
                           @RequestBody @Valid NewEventDto newEventDto) {

        Pageable pageable = PageRequest.of(0, 1);
        NewLocationDto newLocationDto = new NewLocationDto(
                newEventDto.getLocation().getLat(),
                newEventDto.getLocation().getLon());
        LocationDto locationDto = locationService.create(newLocationDto);
        UserDto userDto = userService.getAll(List.of(userId), pageable).get(0);
        CategoryDto categoryDto = categoryService.getById(newEventDto.getCategory());

        return eventService.create(newEventDto, locationDto, userDto, categoryDto);
    }

    @GetMapping("/users/{userId}/events")
    public List<EventDto> getEventsByUserId(@PathVariable Long userId,
                                            @RequestParam(required = false, defaultValue = "0") Integer from,
                                            @RequestParam(required = false, defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        return eventService.getByUserId(userId, pageable);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventDto getEventByUserIdAndEventId(@PathVariable Long userId,
                                               @PathVariable Long eventId) {
        return eventService.getByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventDto updateEventByUserIdAndEventId(@PathVariable Long userId,
                                                  @PathVariable Long eventId,
                                                  @RequestBody UpdateEventDto updateEventDto) {
        return eventService.updateByUserIdAndEventId(userId, eventId, updateEventDto);
    }
}
