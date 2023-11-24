package ru.practicum.ewmmain.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestStatusUpdate {
    @NotBlank(message = "Status отсутствует.")
    private String status;
    @NotNull(message = "RequestIds отсутствуют.")
    private List<Long> requestIds;
}
