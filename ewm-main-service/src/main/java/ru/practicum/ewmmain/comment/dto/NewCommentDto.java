package ru.practicum.ewmmain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCommentDto {
    @NotBlank(message = "Text комментария не должен быть пустой.")
    @Size(max = 1000, min = 20, message = "Длина строки должна быть от 20 до 1000 символов.")
    private String text;
}
