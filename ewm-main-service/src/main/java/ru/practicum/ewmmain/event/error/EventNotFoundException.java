package ru.practicum.ewmmain.event.error;

import javax.persistence.EntityNotFoundException;

public class EventNotFoundException extends EntityNotFoundException {
    public EventNotFoundException(Long id) {
        super(String.format("Event с id=%s не найдена", id));
    }
}
