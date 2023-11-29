package ru.practicum.ewmmain.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmmain.event.dto.EventMinDto;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    private List<EventMinDto> events;
    private Long id;
    private Boolean pinned;
    private String title;
}
