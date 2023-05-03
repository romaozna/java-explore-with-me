package ru.practicum.ewm.compilation.dto;

import lombok.Value;
import ru.practicum.ewm.event.dto.EventDto;

import java.util.List;

@Value
public class CompilationDto {
    Long id;
    Boolean pinned;
    String title;
    List<EventDto> events;
}
