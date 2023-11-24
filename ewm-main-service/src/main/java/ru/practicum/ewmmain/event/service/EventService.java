package ru.practicum.ewmmain.event.service;

import ru.practicum.ewmmain.event.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventDto> getAllForAdmin(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventDto patchEventForAdmin(Long eventId, UpdateEventAdmin updateEventAdmin);

    List<EventMinDto> getAllPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sortParam, Integer from, Integer size, HttpServletRequest request);

    EventDto getOneEventPublic(Long eventId, HttpServletRequest request);

    List<EventDto> getByUserId(Long userId, Integer from, Integer size);

    EventDto getUsersEventById(Long userId, Long eventId);

    EventDto add(Long userId, NewEventDto newEventDto);

    EventDto update(Long userId, Long eventId, UpdateEventPublic updateEventPublic);
}
