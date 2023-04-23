package ru.practicum.ewm.location.dto;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class LocationDto {
    Long id;
    @NotNull
    Float lat;
    @NotNull
    Float lon;
}
