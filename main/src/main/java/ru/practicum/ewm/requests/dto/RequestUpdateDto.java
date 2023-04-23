package ru.practicum.ewm.requests.dto;

import lombok.Value;

import javax.validation.constraints.NotNull;
import java.util.List;

@Value
public class RequestUpdateDto {
    @NotNull
    List<RequestDto> confirmedRequests;
    @NotNull
    List<RequestDto> rejectedRequests;
}
