package ru.practicum.ewmmain.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryDto {
    @NotBlank(message = "Name не может быть пустым.")
    @Size(max = 50, min = 1, message = "Длина строки должна быть от 1 до 50 символов.")
    private String name;
}
