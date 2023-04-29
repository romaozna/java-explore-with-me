package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
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
    public EventDto create(@PathVariable("userId") Long userId,
                           @RequestBody @Valid NewEventDto newEventDto) {
        NewLocationDto newLocationDto = new NewLocationDto(
                newEventDto.getLocation().getLat(),
                newEventDto.getLocation().getLon());
        LocationDto locationDto = locationService.create(newLocationDto);
        UserDto userDto = userService.getAll(List.of(userId), 0, 1).get(0);
        CategoryDto categoryDto = categoryService.getById(newEventDto.getCategory(), 0, 1);

        return eventService.create(newEventDto, locationDto, userDto, categoryDto);
    }

    @GetMapping("/users/{userId}/events")
    public List<EventDto> getEventsByUserId(@PathVariable("userId") Long userId,
                                            @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return eventService.getAllByUserId(userId, from, size);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventDto getEventByUserIdAndEventId(@PathVariable("userId") Long userId,
                                               @PathVariable("eventId") Long eventId) {
        return eventService.getByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventDto updateEventByUserIdAndEventId(@PathVariable("userId") Long userId,
                                                  @PathVariable("eventId") Long eventId,
                                                  @RequestBody UpdateEventUserRequest newEventDto) {
        return eventService.updateByUserIdAndEventId(userId, eventId, newEventDto);
    }
}
