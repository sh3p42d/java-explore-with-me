package ru.practicum.ewmmain.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.compilation.dto.CompilationDto;
import ru.practicum.ewmmain.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.ewmmain.config.exceptions.ErrorMessages.FROM_ERROR_MESSAGE;
import static ru.practicum.ewmmain.config.exceptions.ErrorMessages.SIZE_ERROR_MESSAGE;


@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Validated
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getAllCompilations(@RequestParam(required = false) Boolean pinned,
                                                   @PositiveOrZero(message = FROM_ERROR_MESSAGE)
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @Positive(message = SIZE_ERROR_MESSAGE)
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return compilationService.getAllCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getOneCompilation(@PathVariable Long compId) {
        return compilationService.getOneCompilation(compId);
    }
}
