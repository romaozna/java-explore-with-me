package ru.practicum.ewm.compilation.dto;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class CompilationEventDto {
    @NotNull
    Long compilationId;
    @NotNull
    Long eventId;
}