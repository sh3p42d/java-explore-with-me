package ru.practicum.ewmmain.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.request.dto.ParticipationRequestDto;
import ru.practicum.ewmmain.request.service.RequestService;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
public class RequestPrivateController {
    private final RequestService service;

    @GetMapping
    public List<ParticipationRequestDto> getAll(@PathVariable long userId) {
        return service.getAllByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto add(@PathVariable Long userId,
                                       @NotNull(message = "Отсутствует id события в запросе") @RequestParam Long eventId) {
        return service.addRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable Long userId,
                                          @PathVariable Long requestId) {
        return service.cancelRequest(userId, requestId);
    }
}
