package ru.practicum.ewm.compilation.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getAll(Boolean pinned, Pageable pageable);

    CompilationDto getById(Long compId);

    CompilationDto create(NewCompilationDto newCompilationDto);

    CompilationDto update(Long compId, NewCompilationDto compilationDto);

    void delete(Long compId);
}
