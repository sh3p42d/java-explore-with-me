package ru.practicum.ewmmain.comment.error;

import javax.persistence.EntityNotFoundException;

public class CommentNotAllowed extends EntityNotFoundException {
    public CommentNotAllowed(String status) {
        super(String.format("Comment со статусом %s недоступен", status));
    }
}