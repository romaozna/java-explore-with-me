package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CreatedEndpointHitDto;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatDto;
import ru.practicum.ewm.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EndpointHitServiceImpl implements EndpointHitService {

    private final EndpointHitRepository endpointHitRepository;

    @Override
    @Transactional
    public EndpointHitDto createHit(CreatedEndpointHitDto endpointHitDto) {
        return EndpointHitMapper
                .toEndpointHitDto(endpointHitRepository.save(EndpointHitMapper.toEndpointHit(endpointHitDto)));
    }

    @Override
    public List<ViewStatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (uris.isEmpty()) {
            return unique ? ViewStatMapper.toViewStatDto(endpointHitRepository.getUniqueViewStats(start, end)) :
                    ViewStatMapper.toViewStatDto(endpointHitRepository.getNonUniqueViewStats(start, end));
        }

        return unique ? ViewStatMapper.toViewStatDto(endpointHitRepository.getUniqueViewStats(uris, start, end)) :
                ViewStatMapper.toViewStatDto(endpointHitRepository.getNonUniqueViewStats(uris, start, end));
    }
}
