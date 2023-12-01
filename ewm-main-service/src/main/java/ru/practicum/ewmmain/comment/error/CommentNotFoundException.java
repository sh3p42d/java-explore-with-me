package ru.practicum.ewmmain.comment.error;

import javax.persistence.EntityNotFoundException;

public class CommentNotFoundException extends EntityNotFoundException {
    public CommentNotFoundException(Long id) {
        super(String.format("Comment с id=%s не найден", id));
    }
}
