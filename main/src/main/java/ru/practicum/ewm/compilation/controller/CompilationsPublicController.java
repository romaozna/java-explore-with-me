package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class CompilationsPublicController {
    private final CompilationService compilationService;

    @GetMapping("/compilations")
    public List<CompilationDto> findAll(@RequestParam(required = false) Boolean pinned,
                                        @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                        @Positive @RequestParam(required = false, defaultValue = "20") Integer size) {
        PageRequest pageable = PageRequest.of(from / size, size);
        return compilationService.findAll(pinned, pageable);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto findById(@PathVariable Long compId) {
        return compilationService.findById(compId);
    }
}
