package ru.practicum.ewm.requests.dto;

import lombok.Value;

import javax.validation.constraints.NotNull;
import java.util.List;

@Value
public class NewRequestUpdateDto {
    @NotNull
    List<Long> requestIds;
    @NotNull
    RequestStatus status;
}
