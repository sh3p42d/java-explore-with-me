package ru.practicum.ewmmain.compilation.service;

import ru.practicum.ewmmain.compilation.dto.CompilationDto;
import ru.practicum.ewmmain.compilation.dto.NewCompilationDto;
import ru.practicum.ewmmain.compilation.dto.UpdateCompilation;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size);

    CompilationDto getOneCompilation(long compId);

    CompilationDto createNewCompilation(NewCompilationDto compilation);

    CompilationDto updateCompilation(long compId, UpdateCompilation updateCompilationRequest);

    void deleteCompilation(long compId);
}
