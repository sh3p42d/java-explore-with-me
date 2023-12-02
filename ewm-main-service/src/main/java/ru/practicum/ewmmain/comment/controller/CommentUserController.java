//package ru.practicum.ewmmain.comment.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import ru.practicum.ewmmain.comment.dto.CommentFullDto;
//import ru.practicum.ewmmain.comment.service.CommentService;
//
//import java.util.List;
//
//@RestController
//@RequestMapping(path = "/users/{userId}/comments")
//@RequiredArgsConstructor
//public class CommentUserController {
//
//    private final CommentService commentService;
//
//    @GetMapping
//    public List<CommentFullDto> getAllForUser(@PathVariable long userId) {
//        return commentService.getAllForUser(userId);
//    }
//}
