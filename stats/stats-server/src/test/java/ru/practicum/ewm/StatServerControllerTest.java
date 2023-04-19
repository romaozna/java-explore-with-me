package ru.practicum.ewm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.controller.StatServerController;
import ru.practicum.ewm.dto.CreatedEndpointHitDto;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatDto;
import ru.practicum.ewm.service.EndpointHitService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatServerController.class)
public class StatServerControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private EndpointHitService endpointHitService;

    @Autowired
    private MockMvc mvc;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String DATE = "2022-09-15 19:30:00";

    private final CreatedEndpointHitDto createdEndpointHitDto = new CreatedEndpointHitDto(
            "ewm-main-service",
            "/events",
            "192.168.209.131",
            LocalDateTime.parse(DATE, DATE_TIME_FORMATTER));

    private final EndpointHitDto endpointHitDto = new EndpointHitDto(
            1L,
            createdEndpointHitDto.getApp(),
            createdEndpointHitDto.getUri(),
            createdEndpointHitDto.getIp(),
            createdEndpointHitDto.getTimestamp());

    private final ViewStatDto viewStatDto = new ViewStatDto(
            endpointHitDto.getApp(),
            endpointHitDto.getUri(),
            1L);

    @Test
    void createNewHitTest() throws Exception {
        when(endpointHitService.createHit(any(CreatedEndpointHitDto.class)))
                .thenReturn(endpointHitDto);

        mvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(createdEndpointHitDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.app", is(createdEndpointHitDto.getApp()), String.class))
                .andExpect(jsonPath("$.uri", is(createdEndpointHitDto.getUri()), String.class))
                .andExpect(jsonPath("$.ip", is(createdEndpointHitDto.getIp()), String.class))
                .andExpect(jsonPath("$.timestamp", is(DATE), String.class));
    }

    @Test
    void getStatsTest() throws Exception {
        when(endpointHitService.getStats(any(LocalDateTime.class), any(LocalDateTime.class), any(), any()))
                .thenReturn(List.of(viewStatDto));

        mvc.perform(get("/stats")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("start", "2020-05-05 00:00:00")
                        .param("end", "2023-05-05 00:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].app", is(viewStatDto.getApp()), String.class))
                .andExpect(jsonPath("$[0].uri", is(viewStatDto.getUri()), String.class))
                .andExpect(jsonPath("$[0].hits", is(viewStatDto.getHits()), Long.class));
    }
}
