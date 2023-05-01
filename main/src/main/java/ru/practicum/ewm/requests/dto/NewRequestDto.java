package ru.practicum.ewm.requests.dto;

import lombok.Value;

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
