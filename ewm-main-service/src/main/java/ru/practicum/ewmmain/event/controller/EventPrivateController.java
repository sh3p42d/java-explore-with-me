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
    public List<EventDto> getByUserId(@PathVariable long userId,
                                      @PositiveOrZero(message = FROM_ERROR_MESSAGE)
                                      @RequestParam(defaultValue = "0") int from,
                                      @Positive(message = SIZE_ERROR_MESSAGE)
                                      @RequestParam(defaultValue = "10") int size) {
        return service.getByUserId(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventDto getUsersEventById(@PathVariable long userId,
                                          @PathVariable long eventId) {
        return service.getUsersEventById(userId, eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto add(@PathVariable long userId,
                        @Valid @RequestBody NewEventDto newEventDto) {
        return service.add(userId, newEventDto);
    }

    @PatchMapping("/{eventId}")
    public EventDto update(@PathVariable long userId,
                               @PathVariable long eventId,
                               @Valid @RequestBody UpdateEventPublic updateEvent) {

        return service.update(userId, eventId, updateEvent);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsForUsersEvent(@PathVariable long userId,
                                                                  @PathVariable long eventId) {
        return requestService.getRequestsForUsersEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestStatusUpdateApprove updateRequests(@PathVariable long userId,
                                                     @PathVariable long eventId,
                                                     @Valid @RequestBody RequestStatusUpdate eventRequestStatusUpdateRequest) {
        return requestService.updateRequests(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
