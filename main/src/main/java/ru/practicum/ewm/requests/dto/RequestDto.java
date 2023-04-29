package ru.practicum.ewm.requests.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;
import ru.practicum.ewm.requests.model.RequestStatus;

import java.time.LocalDateTime;

@Value
public class RequestDto {
    Long id;
    Long event;
    Long requester;
    RequestStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime created;
}
