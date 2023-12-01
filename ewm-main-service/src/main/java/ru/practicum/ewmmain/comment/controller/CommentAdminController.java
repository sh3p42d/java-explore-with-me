package ru.practicum.ewmmain.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.comment.dto.CommentFullDto;
import ru.practicum.ewmmain.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/events/{eventId}/comments")
@RequiredArgsConstructor
@Validated
public class CommentAdminController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentFullDto> getCommentsForAdmin(@PathVariable long eventId) {
        return commentService.getCommentsForAdmin(eventId);
    }

    @PatchMapping("/{commId}")
    public CommentFullDto updateCommentByAdmin(@PathVariable long eventId,
                                             @PathVariable long commId,
                                             @RequestParam String actionAdmin) {
        return commentService.updateCommentByAdmin(eventId, commId, actionAdmin);
    }

    @DeleteMapping("/{commId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long eventId,
                              @PathVariable long commId) {
        commentService.deleteComment(eventId, commId);
    }
}
