package ru.practicum.ewmmain.user.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDto {
    @NotBlank(message = "Email не может быть пустым.")
    @Size(max = 254, min = 6, message = "Длина строки должна быть от 6 до 254 символов.")
    @Email(message = "Неверный формат почты mail@domain.com")
    private String email;
    @NotBlank(message = "Name не может быть пустым.")
    @Size(max = 250, min = 2, message = "Длина строки должна быть от 2 до 250 символов.")
    private String name;
}
