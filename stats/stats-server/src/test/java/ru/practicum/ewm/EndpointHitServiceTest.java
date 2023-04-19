package ru.practicum.ewm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.dto.CreatedEndpointHitDto;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatDto;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStat;
import ru.practicum.ewm.repository.EndpointHitRepository;
import ru.practicum.ewm.service.EndpointHitServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class EndpointHitServiceTest {

    @InjectMocks
    private EndpointHitServiceImpl endpointHitService;

    @Mock
    private EndpointHitRepository endpointHitRepository;

    private final CreatedEndpointHitDto createdEndpointHitDto = new CreatedEndpointHitDto(
            "ewm-main-service",
            "/events",
            "192.168.209.131",
            LocalDateTime.now());

    private final ViewStatDto viewStatDto = new ViewStatDto(
            createdEndpointHitDto.getApp(),
            createdEndpointHitDto.getUri(),
            1L);

    private final ViewStat viewStat = new ViewStat(
            createdEndpointHitDto.getUri(),
            createdEndpointHitDto.getApp(),
            1L);

    private final EndpointHitDto endpointHitDto = new EndpointHitDto(
            1L,
            createdEndpointHitDto.getApp(),
            createdEndpointHitDto.getUri(),
            createdEndpointHitDto.getIp(),
            createdEndpointHitDto.getTimestamp());

    private final EndpointHit endpointHit = new EndpointHit(
            1L,
            createdEndpointHitDto.getApp(),
            createdEndpointHitDto.getUri(),
            createdEndpointHitDto.getIp(),
            createdEndpointHitDto.getTimestamp());

    @Test
    void createNewHitTest() {
        when(endpointHitRepository.save(any(EndpointHit.class))).thenReturn(endpointHit);

        EndpointHitDto created = endpointHitService.createHit(createdEndpointHitDto);

        Assertions.assertNotNull(created);
        Assertions.assertEquals(endpointHitDto.getId(), created.getId());
        Assertions.assertEquals(endpointHitDto.getApp(), created.getApp());
        Assertions.assertEquals(endpointHitDto.getUri(), created.getUri());
        Assertions.assertEquals(endpointHitDto.getIp(), created.getIp());
        Assertions.assertEquals(endpointHitDto.getTimestamp(), created.getTimestamp());
    }

    @Test
    void getNonUniqueViewStatsTest() {
        when(endpointHitRepository.getNonUniqueViewStats(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(viewStat));

        List<ViewStatDto> viewStatDtoList = endpointHitService
                .getStats(LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), false);

        Assertions.assertNotNull(viewStatDtoList);
        Assertions.assertEquals(viewStatDto.getApp(), viewStatDtoList.get(0).getApp());
        Assertions.assertEquals(viewStatDto.getUri(), viewStatDtoList.get(0).getUri());
        Assertions.assertEquals(viewStatDto.getHits(), viewStatDtoList.get(0).getHits());

    }

    @Test
    void getUniqueViewStatsTest() {
        when(endpointHitRepository.getUniqueViewStats(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(viewStat));

        List<ViewStatDto> viewStatDtoList = endpointHitService
                .getStats(LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), true);

        Assertions.assertNotNull(viewStatDtoList);
        Assertions.assertEquals(viewStatDto.getApp(), viewStatDtoList.get(0).getApp());
        Assertions.assertEquals(viewStatDto.getUri(), viewStatDtoList.get(0).getUri());
        Assertions.assertEquals(viewStatDto.getHits(), viewStatDtoList.get(0).getHits());

    }

    @Test
    void getNonUniqueViewStatsWithUriTest() {
        when(endpointHitRepository.getNonUniqueViewStats(any(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(viewStat));

        List<ViewStatDto> viewStatDtoList = endpointHitService
                .getStats(LocalDateTime.now(), LocalDateTime.now(), List.of("/event"), false);

        Assertions.assertNotNull(viewStatDtoList);
        Assertions.assertEquals(viewStatDto.getApp(), viewStatDtoList.get(0).getApp());
        Assertions.assertEquals(viewStatDto.getUri(), viewStatDtoList.get(0).getUri());
        Assertions.assertEquals(viewStatDto.getHits(), viewStatDtoList.get(0).getHits());

    }

    @Test
    void getUniqueViewStatsWithUriTest() {
        when(endpointHitRepository.getUniqueViewStats(any(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(viewStat));

        List<ViewStatDto> viewStatDtoList = endpointHitService
                .getStats(LocalDateTime.now(), LocalDateTime.now(), List.of("/event"), true);

        Assertions.assertNotNull(viewStatDtoList);
        Assertions.assertEquals(viewStatDto.getApp(), viewStatDtoList.get(0).getApp());
        Assertions.assertEquals(viewStatDto.getUri(), viewStatDtoList.get(0).getUri());
        Assertions.assertEquals(viewStatDto.getHits(), viewStatDtoList.get(0).getHits());

    }
}
