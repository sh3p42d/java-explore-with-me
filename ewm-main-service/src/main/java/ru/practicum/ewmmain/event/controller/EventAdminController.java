package ru.practicum.ewmmain.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.event.dto.EventDto;
import ru.practicum.ewmmain.event.dto.UpdateEventAdmin;
import ru.practicum.ewmmain.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Past;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewmmain.config.exceptions.ErrorMessages.FROM_ERROR_MESSAGE;
import static ru.practicum.ewmmain.config.exceptions.ErrorMessages.SIZE_ERROR_MESSAGE;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Validated
public class EventAdminController {
    private final EventService eventService;

    @GetMapping
    public List<EventDto> getAllEventsForAdmin(@RequestParam(required = false) List<Long> users,
                                               @RequestParam(required = false) List<String> states,
                                               @RequestParam(required = false) List<Long> categories,
                                               @RequestParam(required = false)
                                               @Past
                                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                               LocalDateTime rangeStart,
                                               @RequestParam(required = false)
                                               @FutureOrPresent
                                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                               LocalDateTime rangeEnd,
                                               @PositiveOrZero(message = FROM_ERROR_MESSAGE)
                                               @RequestParam(defaultValue = "0") int from,
                                               @Positive(message = SIZE_ERROR_MESSAGE)
                                               @RequestParam(defaultValue = "10") int size) {
        return eventService.getAllForAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventDto patchEventForAdmin(@PathVariable long eventId,
                                       @Valid @RequestBody UpdateEventAdmin event) {
        return eventService.patchEventForAdmin(eventId, event);
    }
}
