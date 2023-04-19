package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CreatedEndpointHitDto;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatDto;
import ru.practicum.ewm.service.EndpointHitService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class StatServerController {
    private final EndpointHitService endpointHitService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto create(@RequestBody @Valid CreatedEndpointHitDto endpointHitDto) {
        return endpointHitService.createHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatDto> getStats(@RequestParam("start") LocalDateTime start,
                                      @RequestParam("end") LocalDateTime end,
                                      @RequestParam(value = "uris", required = false, defaultValue = "") List<String> uris,
                                      @RequestParam(value = "unique", required = false, defaultValue = "false") Boolean unique) {
        return endpointHitService.getStats(start, end, uris, unique);
    }
}
