package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequestDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationsRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationsRepository compilationsRepository;
    private final EventRepository eventsRepository;

    @Override
    @Transactional
    public CompilationDto create(UpdateCompilationRequestDto updateCompilationRequestDto) {
        List<Event> events = eventsRepository.findAllById(updateCompilationRequestDto.getEvents());
        Compilation compilation = compilationsRepository.save(CompilationMapper.toCompilation(updateCompilationRequestDto, events));
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public void delete(Long compId) {
        compilationsRepository.findById(compId).orElseThrow(() -> {
            throw new NotFoundException("Compilation not found");
        });
        compilationsRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, UpdateCompilationRequestDto updateCompilationRequestDto) {
        Compilation compilation = compilationsRepository.findById(compId).orElseThrow(() -> {
            throw new NotFoundException("Compilation not found");
        });
        if (updateCompilationRequestDto.getEvents() != null) {
            compilation.setEvents(eventsRepository.findAllById(updateCompilationRequestDto.getEvents()));
        }
        if (updateCompilationRequestDto.getPinned() != null) {
            compilation.setPinned(updateCompilationRequestDto.getPinned());
        }
        if (updateCompilationRequestDto.getTitle() != null) {
            compilation.setTitle(updateCompilationRequestDto.getTitle());
        }
        compilation = compilationsRepository.save(compilation);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public List<CompilationDto> findAll(Boolean pinned, Pageable pageable) {
        return compilationsRepository.findAllByPinned(pinned, pageable).stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto findById(Long compId) {
        Compilation compilation = compilationsRepository.findById(compId).orElseThrow(() -> {
            throw new NotFoundException("Compilation not found");
        });
        return CompilationMapper.toCompilationDto(compilation);
    }
}
