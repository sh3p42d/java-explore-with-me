package ru.practicum.ewmmain.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.event.dto.EventDto;
import ru.practicum.ewmmain.event.dto.NewEventDto;
import ru.practicum.ewmmain.event.dto.UpdateEventPublic;
import ru.practicum.ewmmain.event.service.EventService;
import ru.practicum.ewmmain.request.dto.ParticipationRequestDto;
import ru.practicum.ewmmain.request.dto.RequestStatusUpdate;
import ru.practicum.ewmmain.request.dto.RequestStatusUpdateApprove;
import ru.practicum.ewmmain.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.ewmmain.config.exceptions.ErrorMessages.FROM_ERROR_MESSAGE;
import static ru.practicum.ewmmain.config.exceptions.ErrorMessages.SIZE_ERROR_MESSAGE;


@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class EventPrivateController {
    private final EventService service;
    private final RequestService requestService;

    @GetMapping
    public List<EventDto> getByUserId(@PathVariable Long userId,
                                      @PositiveOrZero(message = FROM_ERROR_MESSAGE)
                                      @RequestParam(defaultValue = "0") Integer from,
                                      @Positive(message = SIZE_ERROR_MESSAGE)
                                      @RequestParam(defaultValue = "10") Integer size) {
        return service.getByUserId(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventDto getUsersEventById(@PathVariable Long userId,
                                          @PathVariable Long eventId) {
        return service.getUsersEventById(userId, eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto add(@PathVariable Long userId,
                        @Valid @RequestBody NewEventDto newEventDto) {
        return service.add(userId, newEventDto);
    }

    @PatchMapping("/{eventId}")
    public EventDto update(@PathVariable Long userId,
                               @PathVariable Long eventId,
                               @Valid @RequestBody UpdateEventPublic updateEvent) {

        return service.update(userId, eventId, updateEvent);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsForUsersEvent(@PathVariable Long userId,
                                                                  @PathVariable Long eventId) {
        return requestService.getRequestsForUsersEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestStatusUpdateApprove updateRequests(@PathVariable Long userId,
                                                     @PathVariable Long eventId,
                                                     @Valid @RequestBody RequestStatusUpdate eventRequestStatusUpdateRequest) {
        return requestService.updateRequests(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
