package ru.practicum.ewmmain.comment.service;

import ru.practicum.ewmmain.comment.dto.CommentDto;
import ru.practicum.ewmmain.comment.dto.CommentFullDto;
import ru.practicum.ewmmain.comment.dto.NewCommentDto;
import ru.practicum.ewmmain.comment.dto.UpdateCommentDto;

import java.util.List;

public interface CommentService {
    List<CommentFullDto> getAllForUser(long userId);

    List<CommentDto> getAllCommentsForEvent(long eventId);

    CommentFullDto createComment(long userId, NewCommentDto newCommentDto);

    CommentDto increaseLikesForComment(long userId, long eventId, long commId);

    List<CommentFullDto> getCommentsForAdmin(long eventId);

    CommentFullDto updateCommentByAdmin(long eventId, long commId, String actionAdmin);

    void deleteComment(long eventId, long commId);

    CommentFullDto updateComment(long userId, UpdateCommentDto updateCommentDto);
}
