package ru.practicum.ewmmain.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.event.dto.EventDto;
import ru.practicum.ewmmain.event.dto.EventMinDto;
import ru.practicum.ewmmain.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewmmain.config.exceptions.ErrorMessages.FROM_ERROR_MESSAGE;
import static ru.practicum.ewmmain.config.exceptions.ErrorMessages.SIZE_ERROR_MESSAGE;


@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Validated
public class EventPublicController {
    private final EventService eventService;

    @GetMapping
    public List<EventMinDto> getAllEventsPublic(@RequestParam(required = false) @Size(max = 2000) String text,
                                                @RequestParam(required = false) List<Long> categories,
                                                @RequestParam(required = false) Boolean paid,
                                                @RequestParam(required = false) @Past @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                @RequestParam(required = false) @FutureOrPresent @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                @RequestParam(defaultValue = "event_date", name = "sort") String sortParam,
                                                @PositiveOrZero(message = FROM_ERROR_MESSAGE) @RequestParam(defaultValue = "0") int from,
                                                @Positive(message = SIZE_ERROR_MESSAGE) @RequestParam(defaultValue = "10") int size,
                                                HttpServletRequest request) {
        eventService.addEndpointHit("/events", request);
        return eventService.getAllPublic(text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable,
                sortParam, from, size, request);
    }

    @GetMapping("/{eventId}")
    public EventDto getOneEventPublic(@PathVariable long eventId,
                                      HttpServletRequest request) {
        eventService.addEndpointHit("/events/" + eventId, request);
        return eventService.getOneEventPublic(eventId, request);
    }
}
