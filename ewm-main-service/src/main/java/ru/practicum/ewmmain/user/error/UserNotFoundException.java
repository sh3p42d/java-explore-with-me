package ru.practicum.ewmmain.user.error;

import javax.persistence.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(Long id) {
        super(String.format("User с id=%s не найден", id));
    }
}
