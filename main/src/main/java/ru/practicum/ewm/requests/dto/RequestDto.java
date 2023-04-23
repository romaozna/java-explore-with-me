package ru.practicum.ewm.requests.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;
import ru.practicum.ewm.event.dto.State;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Value
public class RequestDto {
    @NotNull
    Long id;
    @NotNull
    Long event;
    @NotNull
    Long requester;
    @NotNull
    State status;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime created;
}
