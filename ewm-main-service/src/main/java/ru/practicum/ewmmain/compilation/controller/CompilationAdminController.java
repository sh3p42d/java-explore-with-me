package ru.practicum.ewmmain.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.compilation.dto.CompilationDto;
import ru.practicum.ewmmain.compilation.dto.NewCompilationDto;
import ru.practicum.ewmmain.compilation.dto.UpdateCompilation;
import ru.practicum.ewmmain.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Validated
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createNewCompilation(@Valid @RequestBody NewCompilationDto compilation) {
        return compilationService.createNewCompilation(compilation);
    }

    @PatchMapping("/{compId}")
    public CompilationDto patchCompilation(@PathVariable long compId,
                                           @Valid @RequestBody UpdateCompilation updateCompilation) {
        return compilationService.updateCompilation(compId, updateCompilation);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable long compId) {
        compilationService.deleteCompilation(compId);
    }
}
