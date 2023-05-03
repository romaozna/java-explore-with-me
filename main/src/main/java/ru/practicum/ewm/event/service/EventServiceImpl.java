package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatClient;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.dto.CreatedEndpointHitDto;
import ru.practicum.ewm.dto.ViewStatDto;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventAdminParams;
import ru.practicum.ewm.event.model.EventPublicParams;
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

        event.setState(EventState.PENDING);

        return EventMapper.toEventDto(eventRepository.save(event));
    }

    @Override
    public List<EventDto> getByUserId(Long userId, Pageable pageable) {
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        Map<String, Long> eventViewsMap = getEventViewsMap(getEventsViewsList(events));

        return EventMapper.toEventDto(events, eventViewsMap);
    }

    @Override
    public EventDto getByUserIdAndEventId(Long userId, Long eventId) {
        Event event = getEventByInitiatorIdAndEventId(userId, eventId);
        Map<String, Long> eventViewsMap = getEventViewsMap(getEventsViewsList(List.of(event)));

        return EventMapper.toEventDto(List.of(event), eventViewsMap).get(0);
    }

    @Override
    @Transactional
    public EventDto updateByUserIdAndEventId(Long userId, Long eventId, UpdateEventDto updateEventDto) {
        validateEventDate(updateEventDto.getEventDate(), LocalDateTime.now().plusHours(2));

        Event event = getEventByInitiatorIdAndEventId(userId, eventId);
        updateEventState(event, updateEventDto);

        return EventMapper.toEventDto(event);
    }

    @Override
    @Transactional
    public EventDto updateByEventId(Long eventId, UpdateEventDto updateEventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(EVENT_NOT_FOUND_MESSAGE, eventId)));

        if (updateEventDto == null) {
            return EventMapper.toEventDto(event);
        }

        validateAdminEventDate(updateEventDto.getEventDate(), event);
        updateEventState(event, updateEventDto);
        updateEventDetails(event, updateEventDto);

        return EventMapper.toEventDto(event);
    }

    @Override
    public List<EventDto> getAll(EventAdminParams params, Pageable pageable) {

        List<Event> events = eventRepository.getAll(
                params.getRangeStart(),
                params.getRangeEnd(),
                params.getUsers(),
                params.getStates(),
                params.getCategories(),
                pageable);
        Map<String, Long> eventViewsMap = getEventViewsMap(getEventsViewsList(events));

        return EventMapper.toEventDto(events, eventViewsMap);
    }

    @Override
    public List<EventDto> getPublicEvents(EventPublicParams params, Pageable pageable) {
        List<Event> events = eventRepository.getPublicEvents(
                params.getState(),
                params.getText(),
                params.getCategories(),
                params.getPaid(),
                params.getRangeStart(),
                params.getRangeEnd(),
                pageable);
        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        List<RequestStat> requestStats = requestRepository.getRequestsStats(eventIds);
        Map<String, Long> eventViewsMap = getEventViewsMap(getEventsViewsList(events));
        List<EventDto> eventDtos = EventMapper.toEventDto(events, eventViewsMap);

        sortEvents(params.getSort(), eventDtos);

        return filterEventsByAvailable(eventDtos, requestStats, params.getOnlyAvailable());
    }

    @Override
    public EventDto getPublicEventById(Long eventId) {

        Event event = getEventByEventIdAndState(eventId, EventState.PUBLISHED);
        Map<String, Long> eventViewsMap = getEventViewsMap(getEventsViewsList(List.of(event)));

        return EventMapper.toEventDto(List.of(event), eventViewsMap).get(0);
    }

    @Override
    public void createNewHit(String ip, String url) {
        String serviceName = "ewm-main-service";
        CreatedEndpointHitDto createdEndpointHitDto = new CreatedEndpointHitDto(serviceName, url, ip, LocalDateTime.now());

        statClient.createHit(createdEndpointHitDto);
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

    private void updateEventState(Event event, UpdateEventDto updateEventDto) {
        StateAction stateAction = updateEventDto.getStateAction();

        if (stateAction == null && event.getState().equals(EventState.PUBLISHED)) {
            throw new OperationException(String.format(EVENT_UPDATE_PUBLISHED_MESSAGE,
                    event.getState()));
        }

        if (stateAction == null) {
            return;
        }

        switch (stateAction) {
            case CANCEL_REVIEW:
                event.setState(EventState.CANCELED);
                break;
            case SEND_TO_REVIEW:
                event.setState(EventState.PENDING);
                break;
            case REJECT_EVENT:
                if (event.getState().equals(EventState.PUBLISHED)) {
                    throw new OperationException(String.format(EVENT_CANCEL_EXCEPTION_MESSAGE,
                            event.getState()));
                }
                event.setState(EventState.CANCELED);
                break;
            case PUBLISH_EVENT:
                if (event.getState().equals(EventState.PUBLISHED) || event.getState().equals(EventState.CANCELED)) {
                    throw new OperationException(String.format(EVENT_STATE_EXCEPTION_MESSAGE,
                            event.getState()));
                }
                if (event.getState().equals(EventState.PENDING)) {
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    event.setRequestModeration(true);
                }
                break;
        }
    }

    private List<ViewStatDto> getEventsViewsList(List<Event> events) {
        DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<String> eventUris = events
                .stream()
                .map(e -> String.format("/events/%s", e.getId()))
                .collect(Collectors.toList());
        String start = events.stream()
                .min(Comparator.comparing(Event::getCreatedOn))
                .get()
                .getCreatedOn()
                .format(customFormatter);
        String end = LocalDateTime.now().format(customFormatter);

        return statClient.getStats(start, end, eventUris, false);
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

    private void updateEventDetails(Event event, UpdateEventDto updateEventDto) {
        event.setParticipantLimit(Objects.requireNonNullElse(updateEventDto.getParticipantLimit(), event.getParticipantLimit()));
        event.setTitle(Objects.requireNonNullElse(updateEventDto.getTitle(), event.getTitle()));
        event.setPaid(Objects.requireNonNullElse(updateEventDto.getPaid(), event.getPaid()));
        event.setDescription(Objects.requireNonNullElse(updateEventDto.getDescription(), event.getDescription()));
        event.setEventDate(Objects.requireNonNullElse(updateEventDto.getEventDate(), event.getEventDate()));
        event.setAnnotation(Objects.requireNonNullElse(updateEventDto.getAnnotation(), event.getAnnotation()));
    }

    private Event getEventByInitiatorIdAndEventId(Long userId, Long eventId) {
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId);

        checkEventForNull(event, eventId);

        return event;
    }

    private Event getEventByEventIdAndState(Long eventId, EventState state) {
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
