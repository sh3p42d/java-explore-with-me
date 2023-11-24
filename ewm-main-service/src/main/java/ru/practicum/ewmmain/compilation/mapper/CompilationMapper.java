package ru.practicum.ewmmain.compilation.mapper;

import ru.practicum.ewmmain.compilation.dto.CompilationDto;
import ru.practicum.ewmmain.compilation.dto.NewCompilationDto;
import ru.practicum.ewmmain.compilation.model.Compilation;
import ru.practicum.ewmmain.event.dto.EventMinDto;
import ru.practicum.ewmmain.event.mapper.EventMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static CompilationDto toCompilationDto(Compilation compilation) {
        List<EventMinDto> eventMinDtoList = new ArrayList<>();
        if (compilation.getEvents() != null) {
            eventMinDtoList = compilation.getEvents()
                    .stream()
                    .map(EventMapper::toEventMinDto)
                    .collect(Collectors.toList());
        }


        return CompilationDto.builder()
                .id(compilation.getId())
                .events(eventMinDtoList)
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public static Compilation toCompilation(NewCompilationDto newCompilationDto) {
        return Compilation.builder()
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .build();
    }
}
