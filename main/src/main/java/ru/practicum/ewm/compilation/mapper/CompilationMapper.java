package ru.practicum.ewm.compilation.mapper;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequestDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;

import java.util.List;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static Compilation toCompilation(UpdateCompilationRequestDto updateCompilationRequestDto, List<Event> events) {
        return new Compilation(null,
                updateCompilationRequestDto.getPinned(),
                updateCompilationRequestDto.getTitle(),
                events);
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(compilation.getId(),
                compilation.getEvents().stream()
                        .map(EventMapper::toEventDto)
                        .collect(Collectors.toList()),
                compilation.getPinned(),
                compilation.getTitle());
    }
}
