package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventState;
import ru.practicum.ewm.event.dto.SortVariant;
import ru.practicum.ewm.event.model.EventPublicParams;
import ru.practicum.ewm.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventPublicController {

    private final EventService eventService;

    @GetMapping("/events")
    public List<EventDto> getEvents(@RequestParam(required = false)
                                    String text,
                                    @RequestParam(required = false)
                                    List<Long> categories,
                                    @RequestParam(required = false)
                                    Boolean paid,
                                    @RequestParam(required = false)
                                    LocalDateTime rangeStart,
                                    @RequestParam(required = false)
                                    LocalDateTime rangeEnd,
                                    @RequestParam(defaultValue = "false")
                                    Boolean onlyAvailable,
                                    @RequestParam(required = false) SortVariant sort,
                                    @RequestParam(required = false, defaultValue = "0") Integer from,
                                    @RequestParam(required = false, defaultValue = "10") Integer size,
                                    HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String url = request.getRequestURI();
        Pageable pageable = PageRequest.of(from, size);
        EventPublicParams eventPublicParams = new EventPublicParams(
                EventState.PUBLISHED,
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort);

        List<EventDto> events = eventService.getPublicEvents(eventPublicParams, pageable);
        eventService.createNewHit(ip, url);

        return events;
    }

    @GetMapping("/events/{eventId}")
    public EventDto getEventById(@PathVariable Long eventId,
                                 HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        String url = request.getRequestURI();

        EventDto publicEventById = eventService.getPublicEventById(eventId);
        eventService.createNewHit(ip, url);

        return  publicEventById;
    }
}
