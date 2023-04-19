package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.CreatedEndpointHitDto;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitService {
    EndpointHitDto createHit(CreatedEndpointHitDto endpointHitDto);

    List<ViewStatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
