package ru.practicum.ewmmain.comment.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmmain.comment.dto.CommentDto;
import ru.practicum.ewmmain.comment.dto.CommentFullDto;
import ru.practicum.ewmmain.comment.dto.NewCommentDto;
import ru.practicum.ewmmain.comment.model.Comment;
import ru.practicum.ewmmain.comment.model.CommentStatusEnum;
import ru.practicum.ewmmain.event.mapper.EventMapper;
import ru.practicum.ewmmain.event.model.Event;
import ru.practicum.ewmmain.user.mapper.UserMapper;
import ru.practicum.ewmmain.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {

    public static CommentFullDto toCommentFullDto(Comment comment) {
        return CommentFullDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(UserMapper.toUserMinDto(comment.getAuthor()))
                .event(EventMapper.toEventCommentDto(comment.getEvent()))
                .created(comment.getCreated())
                .updated(comment.getUpdated())
                .likes(comment.getLikes())
                .status(comment.getStatus())
                .build();
    }

    public static Comment toComment(NewCommentDto newCommentDto, User user, Event event) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .author(user)
                .event(event)
                .likes(0)
                .status(CommentStatusEnum.PENDING)
                .created(LocalDateTime.now())
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .author(UserMapper.toUserMinDto(comment.getAuthor()))
                .created(comment.getCreated())
                .updated(comment.getUpdated())
                .likes(comment.getLikes())
                .text(comment.getText())
                .build();
    }
}
