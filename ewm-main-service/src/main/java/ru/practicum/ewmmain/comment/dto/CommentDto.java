package ru.practicum.ewmmain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmmain.user.dto.UserMinDto;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String text;
    private UserMinDto author;
    private LocalDateTime created;
    private LocalDateTime updated;
    private Integer likes;
}
