package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    public static final String COMPILATION_NOT_FOUND_MESSAGE = "Compilation with id=%s was not found";
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;


    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAll(Boolean pinned, Pageable pageable) {

        return CompilationMapper
                .toCompilationDto(compilationRepository.findByPinned(pinned, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getById(Long compId) {
        Compilation compilation = compilationRepository.findCompilationById(compId)
                .orElseThrow(() -> new NotFoundException(String.format(COMPILATION_NOT_FOUND_MESSAGE, compId)));

        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationRepository
                .save(CompilationMapper.toCompilation(newCompilationDto));
        List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());

        compilation.getEvents().addAll(events);

        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationRepository.findCompilationById(compId)
                .orElseThrow(() -> new NotFoundException(String.format(COMPILATION_NOT_FOUND_MESSAGE, compId)));
        List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());

        compilation.setTitle(Objects.requireNonNullElse(newCompilationDto.getTitle(),
                compilation.getTitle()));
        compilation.setPinned(Objects.requireNonNullElse(newCompilationDto.getPinned(),
                compilation.getPinned()));
        compilation.setEvents(new HashSet<>(events));

        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void delete(Long compId) {
        Integer count = compilationRepository.deleteCompilationById(compId);

        if (count == 0) {
            throw new NotFoundException(String.format(COMPILATION_NOT_FOUND_MESSAGE, compId));
        }
    }
}
