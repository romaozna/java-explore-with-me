package ru.practicum.ewm.requests.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.requests.dto.NewRequestUpdateDto;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.dto.RequestUpdateDto;
import ru.practicum.ewm.requests.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RequestPrivateController {
    private final RequestService requestService;

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto create(@PathVariable Long userId,
                             @RequestParam Long eventId) {
        return requestService.create(userId, eventId);
    }

    @GetMapping("/users/{userId}/requests")
    public List<RequestDto> getRequestsById(@PathVariable Long userId) {
        return requestService.getAllById(userId);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<RequestDto> getRequestsByUserIdAndEventId(@PathVariable Long userId,
                                                          @PathVariable Long eventId) {
        return requestService.getAllByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable Long userId,
                                    @PathVariable Long requestId) {
        return requestService.cancel(userId, requestId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public RequestUpdateDto updateRequestStatus(@PathVariable Long userId,
                                                @PathVariable Long eventId,
                                                @RequestBody(required = false) NewRequestUpdateDto newRequestUpdateDto) {
        return requestService.updateStatus(userId, eventId, newRequestUpdateDto);
    }
}
