package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventState;
import ru.practicum.ewm.event.dto.UpdateEventDto;
import ru.practicum.ewm.event.model.EventAdminParams;
import ru.practicum.ewm.event.service.EventService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class EventAdminController {

    private final EventService eventService;

    @PatchMapping("/admin/events/{eventId}")
    public EventDto updateEventByUserIdAndEventId(@PathVariable("eventId") Long eventId,
                                                  @Valid @RequestBody UpdateEventDto updateEventDto) {
        return eventService.updateByEventId(eventId, updateEventDto);
    }

    @GetMapping("/admin/events")
    public List<EventDto> getEvents(@RequestParam(value = "rangeStart", required = false)
                                    LocalDateTime rangeStart,
                                    @RequestParam(value = "rangeEnd", required = false)
                                    LocalDateTime rangeEnd,
                                    @RequestParam(value = "users", required = false)
                                    List<Long> users,
                                    @RequestParam(value = "states", required = false)
                                    List<EventState> states,
                                    @RequestParam(value = "categories", required = false)
                                    List<Long> categories,
                                    @RequestParam(value = "from", required = false, defaultValue = "0")
                                    Integer from,
                                    @RequestParam(value = "size", required = false, defaultValue = "10")
                                    Integer size) {

        Pageable pageable = PageRequest.of(from, size);
        EventAdminParams eventParams = new EventAdminParams(states, categories, users, rangeStart, rangeEnd);
        return eventService.getAll(eventParams, pageable);
    }
}
