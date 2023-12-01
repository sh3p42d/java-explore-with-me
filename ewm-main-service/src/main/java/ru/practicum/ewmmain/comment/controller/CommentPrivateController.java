package ru.practicum.ewmmain.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.comment.dto.CommentDto;
import ru.practicum.ewmmain.comment.dto.CommentFullDto;
import ru.practicum.ewmmain.comment.dto.NewCommentDto;
import ru.practicum.ewmmain.comment.service.CommentService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users/{userId}/events/{eventId}/comments")
@RequiredArgsConstructor
@Validated
public class CommentPrivateController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto postComment(@PathVariable long userId,
                                      @PathVariable long eventId,
                                      @Valid @RequestBody NewCommentDto newCommentDto) {
        return commentService.createComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{commId}")
    public CommentFullDto updateComment(@PathVariable long userId,
                                      @PathVariable long eventId,
                                      @PathVariable long commId,
                                      @Valid @RequestBody NewCommentDto newCommentDto) {
        return commentService.updateComment(userId, eventId, commId, newCommentDto);
    }

    @PatchMapping("/{commId}/likes")
    public CommentDto updateCommentLikes(@PathVariable long userId,
                                  @PathVariable long eventId,
                                  @PathVariable long commId) {
        return commentService.increaseLikesForComment(userId, eventId, commId);
    }
}
