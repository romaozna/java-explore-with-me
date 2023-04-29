package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatClient;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.dto.CreatedEndpointHitDto;
import ru.practicum.ewm.dto.ViewStatDto;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.SortVariant;
import ru.practicum.ewm.event.dto.State;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.OperationException;
import ru.practicum.ewm.location.dto.LocationDto;
import ru.practicum.ewm.requests.model.RequestStat;
import ru.practicum.ewm.requests.repository.RequestRepository;
import ru.practicum.ewm.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    public static final String EVENT_NOT_FOUND_MESSAGE = "Event with id=%s was not found";
    public static final String OPERATION_EXCEPTION_MESSAGE = "Field: eventDate. Error: должно содержать дату, " +
            "которая еще не наступила. Value: %s";
    public static final String EVENT_UPDATE_PUBLISHED_MESSAGE = "Cannot update the event because " +
            "it's not in the right state: %s";
    public static final String EVENT_STATE_EXCEPTION_MESSAGE = "Cannot publish the event because " +
            "it's not in the right state: %s";
    public static final String EVENT_CANCEL_EXCEPTION_MESSAGE = "Cannot cancel the event because " +
            "it's not in the right state: %s";

    private final RequestRepository requestRepository;

    private final EventRepository eventRepository;

    private final StatClient statClient;

    @Override
    @Transactional
    public EventDto create(NewEventDto newEventDto, LocationDto locationDto,
                           UserDto userDto, CategoryDto categoryDto) {
        validateEventDate(newEventDto.getEventDate(), LocalDateTime.now().plusHours(2));

        Event event = EventMapper.toEvent(newEventDto, locationDto, userDto, categoryDto);

        event.setState(State.PENDING);

        return EventMapper.toEventDto(eventRepository.save(event));
    }

    @Override
    public List<EventDto> getEventsByUserId(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        Map<String, Long> eventViewsMap = getEventViewsMap(getEventsViewsList(events));

        return EventMapper.toEventDto(events, eventViewsMap);
    }

    @Override
    public EventDto getEventByUserIdAndEventId(Long userId, Long eventId) {
        Event event = getEventByInitiatorIdAndEventId(userId, eventId);
        Map<String, Long> eventViewsMap = getEventViewsMap(getEventsViewsList(List.of(event)));

        return EventMapper.toEventDto(List.of(event), eventViewsMap).get(0);
    }

    @Override
    @Transactional
    public EventDto updateEventByUserIdAndEventId(Long userId, Long eventId, NewEventDto newEventDto) {
        validateEventDate(newEventDto.getEventDate(), LocalDateTime.now().plusHours(2));

        Event event = getEventByInitiatorIdAndEventId(userId, eventId);
        Map<String, Long> eventViewsMap = getEventViewsMap(getEventsViewsList(List.of(event)));

        updateEventState(event, newEventDto);

        return EventMapper.toEventDto(List.of(event), eventViewsMap).get(0);
    }

    @Override
    @Transactional
    public EventDto updateEventByEventId(Long eventId, NewEventDto newEventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(EVENT_NOT_FOUND_MESSAGE, eventId)));

        if (newEventDto == null) {
            return EventMapper.toEventDto(event);
        }

        validateAdminEventDate(newEventDto.getEventDate(), event);
        updateEventState(event, newEventDto);
        updateEventDetails(event, newEventDto);

        Map<String, Long> eventViewsMap = getEventViewsMap(getEventsViewsList(List.of(event)));

        return EventMapper.toEventDto(List.of(event), eventViewsMap).get(0);
    }

    @Override
    public List<EventDto> getEvents(LocalDateTime rangeStart, LocalDateTime rangeEnd, List<Long> users,
                                    List<State> states, List<Long> categories, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        List<Event> events = eventRepository.getEvents(rangeStart, rangeEnd,
                users, states, categories, pageable);
        Map<String, Long> eventViewsMap = getEventViewsMap(getEventsViewsList(events));

        return EventMapper.toEventDto(events, eventViewsMap);
    }

    @Override
    public List<EventDto> getPublicEvents(Integer from, Integer size, State state,
                                          String text, List<Long> categories, Boolean paid,
                                          LocalDateTime rangeStart, LocalDateTime rangeEnd, SortVariant sortVariant,
                                          Boolean onlyAvailable, String ip, String url) {
        createNewHit(ip, url);

        Pageable pageable = PageRequest.of(from, size);
        List<Event> events = eventRepository
                .getPublicEvents(pageable, state, text, categories, paid, rangeStart, rangeEnd);
        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        List<RequestStat> requestStats = requestRepository.getRequestsStats(eventIds);
        Map<String, Long> eventViewsMap = getEventViewsMap(getEventsViewsList(events));
        List<EventDto> eventDtos = EventMapper.toEventDto(events, eventViewsMap);

        sortEvents(sortVariant, eventDtos);

        return filterEventsByAvailable(eventDtos, requestStats, onlyAvailable);
    }

    @Override
    public EventDto getPublicEventById(Long eventId, String ip, String url) {
        createNewHit(ip, url);

        Event event = getEventByEventIdAndState(eventId, State.PUBLISHED);
        Map<String, Long> eventViewsMap = getEventViewsMap(getEventsViewsList(List.of(event)));

        return EventMapper.toEventDto(List.of(event), eventViewsMap).get(0);
    }

    private void validateEventDate(LocalDateTime eventDate, LocalDateTime limit) {
        if (eventDate != null && eventDate.isBefore(limit)) {
            String error = String.format(OPERATION_EXCEPTION_MESSAGE, eventDate);
            throw new OperationException(error);
        }
    }

    private void validateAdminEventDate(LocalDateTime eventDate, Event event) {
        if (event.getCreatedOn() != null
                && eventDate != null
                && eventDate.plusHours(1).isBefore(event.getCreatedOn())) {
            String error = String.format(OPERATION_EXCEPTION_MESSAGE, eventDate);
            throw new OperationException(error);
        }
    }

    private void updateEventState(Event event, NewEventDto newEventDto) {
        State stateAction = newEventDto.getStateAction();

        if (stateAction == null && event.getState().equals(State.PUBLISHED)) {
            throw new OperationException(String.format(EVENT_UPDATE_PUBLISHED_MESSAGE,
                    event.getState()));
        }

        if (stateAction == null) {
            return;
        }

        switch (stateAction) {
            case CANCEL_REVIEW:
                event.setState(State.CANCELED);
                break;
            case SEND_TO_REVIEW:
                event.setState(State.PENDING);
                break;
            case REJECT_EVENT:
                if (event.getState().equals(State.PUBLISHED)) {
                    throw new OperationException(String.format(EVENT_CANCEL_EXCEPTION_MESSAGE,
                            event.getState()));
                }
                event.setState(State.CANCELED);
                break;
            case PUBLISH_EVENT:
                if (event.getState().equals(State.PUBLISHED) || event.getState().equals(State.CANCELED)) {
                    throw new OperationException(String.format(EVENT_STATE_EXCEPTION_MESSAGE,
                            event.getState()));
                }
                if (event.getState().equals(State.PENDING)) {
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    event.setRequestModeration(true);
                }
                break;
            default:
                break;
        }
    }

    private List<ViewStatDto> getEventsViewsList(List<Event> events) {
        DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<String> eventUris = events
                .stream()
                .map(e -> String.format("/events/%s", e.getId()))
                .collect(Collectors.toList());
        String start = LocalDateTime.now().minusYears(2).format(customFormatter);
        String end = LocalDateTime.now().plusYears(2).format(customFormatter);

        return statClient
                .getStats(start, end, eventUris, false);
    }

    private void createNewHit(String ip, String url) {
        String serviceName = "ewm-main-service";
        CreatedEndpointHitDto createdEndpointHitDto = new CreatedEndpointHitDto(serviceName, url, ip, LocalDateTime.now());

        statClient.createHit(createdEndpointHitDto);
    }

    private void sortEvents(SortVariant sortVariant, List<EventDto> eventDtos) {
        Comparator<EventDto> comparatorViews = Comparator.comparing(EventDto::getViews).reversed();
        Comparator<EventDto> comparatorDates = Comparator.comparing(EventDto::getEventDate).reversed();

        if (sortVariant == null) {
            return;
        }

        if (sortVariant.equals(SortVariant.EVENT_DATE)) {
            eventDtos.sort(comparatorDates);
        } else if (sortVariant.equals(SortVariant.VIEWS)) {
            eventDtos.sort(comparatorViews);
        }
    }

    private Map<String, Long> getEventViewsMap(List<ViewStatDto> viewStatDtosList) {
        Map<String, Long> eventViews = new HashMap<>();


        for (ViewStatDto viewStat : viewStatDtosList) {
            eventViews.put(viewStat.getUri(), viewStat.getHits());
        }

        return eventViews;
    }

    private Map<Long, Long> getRequestStatsMap(List<RequestStat> requestStats) {
        Map<Long, Long> requestStatsMap = new HashMap<>();

        for (RequestStat requestStat : requestStats) {
            requestStatsMap.put(requestStat.getEventId(), requestStat.getRequests());
        }

        return requestStatsMap;
    }

    private List<EventDto> filterEventsByAvailable(List<EventDto> eventDtos,
                                                   List<RequestStat> requestStats, Boolean onlyAvailable) {
        if (!onlyAvailable || requestStats.isEmpty()) {
            return eventDtos;
        }

        Map<Long, Long> requestStatsMap = getRequestStatsMap(requestStats);

        return eventDtos
                .stream()
                .filter(eventDto -> requestStatsMap.get(eventDto.getId()) < eventDto.getParticipantLimit())
                .collect(Collectors.toList());
    }

    private void updateEventDetails(Event event, NewEventDto newEventDto) {
        event.setParticipantLimit(Objects.requireNonNullElse(newEventDto.getParticipantLimit(), event.getParticipantLimit()));
        event.setTitle(Objects.requireNonNullElse(newEventDto.getTitle(), event.getTitle()));
        event.setPaid(Objects.requireNonNullElse(newEventDto.getPaid(), event.getPaid()));
        event.setDescription(Objects.requireNonNullElse(newEventDto.getDescription(), event.getDescription()));
        event.setEventDate(Objects.requireNonNullElse(newEventDto.getEventDate(), event.getEventDate()));
        event.setAnnotation(Objects.requireNonNullElse(newEventDto.getAnnotation(), event.getAnnotation()));
    }

    private Event getEventByInitiatorIdAndEventId(Long userId, Long eventId) {
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId);

        checkEventForNull(event, eventId);

        return event;
    }

    private Event getEventByEventIdAndState(Long eventId, State state) {
        Event event = eventRepository.findEventByIdAndState(eventId, state);

        checkEventForNull(event, eventId);

        return event;
    }

    private void checkEventForNull(Event event, Long eventId) {
        if (event == null) {
            throw new NotFoundException(String.format(EVENT_NOT_FOUND_MESSAGE, eventId));
        }
    }
}
