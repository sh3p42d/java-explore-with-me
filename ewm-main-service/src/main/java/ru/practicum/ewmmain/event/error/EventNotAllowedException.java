package ru.practicum.ewmmain.event.error;

import javax.persistence.EntityNotFoundException;

public class EventNotAllowedException extends EntityNotFoundException {
    public EventNotAllowedException(String status) {
        super(String.format("Event со статусом %s недоступен", status));
    }
}
