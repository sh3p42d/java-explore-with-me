package ru.practicum.ewmmain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmmain.comment.model.CommentStatusEnum;
import ru.practicum.ewmmain.event.dto.EventCommentDto;
import ru.practicum.ewmmain.user.dto.UserMinDto;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentFullDto {
    private Long id;
    private String text;
    private UserMinDto author;
    private EventCommentDto event;
    private CommentStatusEnum status;
    private Integer likes;
    private LocalDateTime created;
}
