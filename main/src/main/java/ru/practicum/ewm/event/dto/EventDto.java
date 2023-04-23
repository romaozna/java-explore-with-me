package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.location.dto.NewLocationDto;
import ru.practicum.ewm.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Value
public class EventDto {
    @NotNull
    Long id;
    @NotBlank
    @Size(min = 20, max = 2000)
    String annotation;
    @NotNull
    CategoryDto category;
    Integer confirmedRequests;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdOn;
    @NotBlank
    @Size(min = 20, max = 7000)
    String description;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    @NotNull
    UserShortDto initiator;
    @NotNull
    NewLocationDto location;
    Boolean paid;
    Integer participantLimit;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime publishedOn;
    Boolean requestModeration;
    State state;
    @NotBlank
    @Size(min = 3, max = 120)
    String title;
    Long views;
}
