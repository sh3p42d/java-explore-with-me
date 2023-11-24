package ru.practicum.ewmmain.request.error;

import javax.persistence.EntityNotFoundException;

public class RequestNotFoundException extends EntityNotFoundException {
    public RequestNotFoundException(Long id) {
        super(String.format("Request с id=%s не найден", id));
    }
}
