package ru.practicum.ewm.requests.dto;

import lombok.Value;
import ru.practicum.ewm.event.dto.State;

import javax.validation.constraints.NotNull;

@Value
public class NewRequestDto {
    @NotNull
    Long event;
    @NotNull
    Long requester;
    @NotNull
    State status;
}
