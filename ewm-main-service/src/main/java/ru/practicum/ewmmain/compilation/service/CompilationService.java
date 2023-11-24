package ru.practicum.ewmmain.compilation.service;

import ru.practicum.ewmmain.compilation.dto.CompilationDto;
import ru.practicum.ewmmain.compilation.dto.NewCompilationDto;
import ru.practicum.ewmmain.compilation.dto.UpdateCompilation;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getOneCompilation(Long compId);

    CompilationDto createNewCompilation(NewCompilationDto compilation);

    CompilationDto patchCompilation(Long compId, UpdateCompilation updateCompilationRequest);

    void deleteCompilation(Long compId);
}
