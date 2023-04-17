package ru.practicum.ewm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CreatedEndpointHitDto;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.service.EndpointHitService;

import java.time.LocalDateTime;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class EndpointHitServiceIntegrationTest {

    @Autowired
    private EndpointHitService endpointHitService;

    private final CreatedEndpointHitDto createdEndpointHitDto = new CreatedEndpointHitDto(
            "ewm-main-service",
            "/events",
            "192.168.209.131",
            LocalDateTime.now());

    @Test
    void createNewHitTest() {
        EndpointHitDto endpointHitDto = endpointHitService
                .createHit(createdEndpointHitDto);

        Assertions.assertEquals(1L, endpointHitDto.getId());
        Assertions.assertEquals(createdEndpointHitDto.getApp(), endpointHitDto.getApp());
        Assertions.assertEquals(createdEndpointHitDto.getUri(), endpointHitDto.getUri());
        Assertions.assertEquals(createdEndpointHitDto.getIp(), endpointHitDto.getIp());
        Assertions.assertTrue(createdEndpointHitDto.getTimestamp().isEqual(endpointHitDto.getTimestamp()));
    }
}
