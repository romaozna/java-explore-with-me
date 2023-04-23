package ru.practicum.ewm.compilation.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequestDto;

import java.util.List;

public interface CompilationService {
    CompilationDto create(UpdateCompilationRequestDto updateCompilationRequestDto);

    void delete(Long compId);

    CompilationDto update(Long compId, UpdateCompilationRequestDto updateCompilationRequestDto);

    List<CompilationDto> findAll(Boolean pinned, Pageable pageable);

    CompilationDto findById(Long compId);
}
