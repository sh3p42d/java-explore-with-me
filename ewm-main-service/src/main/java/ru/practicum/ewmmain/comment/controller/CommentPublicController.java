package ru.practicum.ewmmain.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewmmain.comment.dto.CommentDto;
import ru.practicum.ewmmain.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping(path = "/events/{eventId}/comments")
@RequiredArgsConstructor
public class CommentPublicController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getAllCommentsForEvent(@PathVariable long eventId) {
        return commentService.getAllCommentsForEvent(eventId);
    }
}
