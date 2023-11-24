package ru.practicum.ewmmain.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilation {
    private List<Long> events;
    private Boolean pinned;
    @Size(max = 50, min = 1, message = "Длина строки должна быть от 1 до 50 символов.")
    private String title;
}
