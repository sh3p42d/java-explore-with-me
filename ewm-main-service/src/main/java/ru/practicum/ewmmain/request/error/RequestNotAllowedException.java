package ru.practicum.ewmmain.request.error;

import javax.persistence.EntityNotFoundException;

public class RequestNotAllowedException extends EntityNotFoundException {
    public RequestNotAllowedException(Long id, String message) {
        super(String.format("Request для Event с id=%s недоступен: %s", id, message));
    }
}
