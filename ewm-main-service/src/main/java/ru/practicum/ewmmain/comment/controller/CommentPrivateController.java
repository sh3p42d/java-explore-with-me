package ru.practicum.ewmmain.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.comment.dto.CommentDto;
import ru.practicum.ewmmain.comment.dto.CommentFullDto;
import ru.practicum.ewmmain.comment.dto.NewCommentDto;
import ru.practicum.ewmmain.comment.dto.UpdateCommentDto;
import ru.practicum.ewmmain.comment.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}")
@RequiredArgsConstructor
@Validated
public class CommentPrivateController {

    private final CommentService commentService;

    @GetMapping("/comments")
    public List<CommentFullDto> getAllForUser(@PathVariable long userId) {
        return commentService.getAllForUser(userId);
    }

    @PostMapping("/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto postComment(@PathVariable long userId,
                                      @Valid @RequestBody NewCommentDto newCommentDto) {
        return commentService.createComment(userId, newCommentDto);
    }

    @PatchMapping("/comments")
    public CommentFullDto updateComment(@PathVariable long userId,
                                      @Valid @RequestBody UpdateCommentDto updateCommentDto) {
        return commentService.updateComment(userId, updateCommentDto);
    }

    @PatchMapping("/events/{eventId}/comments/{commId}/likes")
    public CommentDto updateCommentLikes(@PathVariable long userId,
                                  @PathVariable long eventId,
                                  @PathVariable long commId) {
        return commentService.increaseLikesForComment(userId, eventId, commId);
    }
}
