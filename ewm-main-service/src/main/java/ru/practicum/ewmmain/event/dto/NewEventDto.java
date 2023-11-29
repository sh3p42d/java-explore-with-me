package ru.practicum.ewmmain.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmmain.location.dto.LocationDto;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotBlank(message = "Annotation не может быть пустой.")
    @Size(max = 2000, min = 20, message = "Длина строки должна быть от 20 до 2000 символов.")
    private String annotation;
    @NotNull(message = "Category не может быть пустой.")
    @Positive(message = "Category ID не может равняться 0.")
    private Long category;
    @NotBlank(message = "Description не может быть пустой.")
    @Size(max = 7000, min = 20, message = "Длина строки должна быть от 20 до 7000 символов.")
    private String description;
    @NotNull(message = "EventDate не может быть пустым.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @FutureOrPresent(message = "EventDate не может быть в прошлом.")
    private LocalDateTime eventDate;
    @NotNull(message = "Location не может быть пустой.")
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    @NotBlank(message = "Title не может быть пустой.")
    @Size(max = 120, min = 3, message = "Длина строки должна быть от 3 до 120 символов.")
    private String title;
}
