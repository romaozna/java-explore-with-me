package ru.practicum.ewm.requests.dto;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class RequestStatDto {
    @NotNull
    Long eventId;
    @NotNull Long requests;
}
