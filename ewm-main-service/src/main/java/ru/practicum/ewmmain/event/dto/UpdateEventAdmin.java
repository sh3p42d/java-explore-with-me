package ru.practicum.ewmmain.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewmmain.location.dto.LocationDto;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdmin {
    @Size(max = 2000, min = 20, message = "Длина строки должна быть от 20 до 2000 символов.")
    private String annotation;
    private Long category;
    @Size(max = 7000, min = 20, message = "Длина строки должна быть от 20 до 7000 символов.")
    private String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @FutureOrPresent(message = "EventDate не может быть в прошлом.")
    private LocalDateTime eventDate;
    private LocationDto locationDto;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private AdminEventState stateAction;
    @Size(max = 120, min = 3, message = "Длина строки должна быть от 3 до 120 символов.")
    private String title;
}
