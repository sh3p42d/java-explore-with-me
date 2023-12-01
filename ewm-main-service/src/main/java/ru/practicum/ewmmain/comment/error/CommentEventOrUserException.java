package ru.practicum.ewmmain.comment.error;

public class CommentEventOrUserException extends RuntimeException {
    public CommentEventOrUserException(String message) {
        super(message);
    }
}
