package ru.practicum.ewm.requests.dto;

import lombok.Value;
import ru.practicum.ewm.requests.model.RequestStatus;

import javax.validation.constraints.NotNull;

@Value
public class NewRequestDto {
    @NotNull
    Long event;
    @NotNull
    Long requester;
    @NotNull
    RequestStatus status;
}
