package ru.practicum.ewmmain.event.service;

import ru.practicum.ewmmain.event.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventDto> getAllForAdmin(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    EventDto patchEventForAdmin(long eventId, UpdateEventAdmin updateEventAdmin);

    List<EventMinDto> getAllPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sortParam, int from, int size, HttpServletRequest request);

    EventDto getOneEventPublic(long eventId, HttpServletRequest request);

    List<EventDto> getByUserId(long userId, int from, int size);

    EventDto getUsersEventById(long userId, long eventId);

    EventDto add(long userId, NewEventDto newEventDto);

    EventDto update(long userId, long eventId, UpdateEventPublic updateEventPublic);

    void addEndpointHit(String uri, HttpServletRequest request);
}
