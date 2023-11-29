package ru.practicum.ewmmain.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.ewmmain.compilation.dto.NewCompilationDto;
import ru.practicum.ewmmain.compilation.dto.UpdateCompilation;
import ru.practicum.ewmmain.compilation.error.CompilationExistsException;
import ru.practicum.ewmmain.compilation.error.CompilationNotFoundException;
import ru.practicum.ewmmain.compilation.mapper.CompilationMapper;
import ru.practicum.ewmmain.compilation.model.Compilation;
import ru.practicum.ewmmain.compilation.repository.CompilationRepository;
import ru.practicum.ewmmain.event.dto.EventMinDto;
import ru.practicum.ewmmain.event.model.Event;
import ru.practicum.ewmmain.event.repository.EventRepository;
import ru.practicum.ewmmain.request.model.RequestStatusEnum;
import ru.practicum.ewmmain.request.repository.RequestRepository;
import ru.practicum.ewmmain.compilation.dto.CompilationDto;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size) {
        PageRequest page = PageRequest.of(from / size, size);
        List<Compilation> compilationsFromDb;

        if (pinned != null) {
            compilationsFromDb = compilationRepository.findAllByPinned(pinned, page).getContent();
        } else {
            compilationsFromDb = compilationRepository.findAll(page).getContent();
        }

        if (compilationsFromDb.isEmpty()) {
            return Collections.emptyList();
        }

        List<CompilationDto> compilationDtoList = compilationsFromDb.stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());

        List<Long> eventIdList = requestRepository.findAllByStatus(RequestStatusEnum.CONFIRMED)
                .stream()
                .map(request -> request.getEvent().getId())
                .collect(Collectors.toList());

        for (CompilationDto compilationDto : compilationDtoList) {
            for (EventMinDto event : compilationDto.getEvents()) {
                Long eventId = event.getId();

                int count = Collections.frequency(eventIdList, eventId);
                if (count > 0) {
                    event.setConfirmedRequests(count);
                }
            }
        }

        return compilationDtoList;
    }

    @Override
    public CompilationDto getOneCompilation(long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new CompilationNotFoundException(compId)
        );

        CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation);

        List<Long> eventIdList = requestRepository.findAllByStatus(RequestStatusEnum.CONFIRMED)
                .stream()
                .map(request -> request.getEvent().getId())
                .collect(Collectors.toList());

        for (EventMinDto event : compilationDto.getEvents()) {
            Long eventId = event.getId();

            int count = Collections.frequency(eventIdList, eventId);
            if (count > 0) {
                event.setConfirmedRequests(count);
            }
        }

        return compilationDto;
    }

    @Override
    @Transactional
    public CompilationDto createNewCompilation(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);

        Compilation compilationByName = compilationRepository.findByTitle(newCompilationDto.getTitle());

        if (compilationByName != null) {
            throw new CompilationExistsException(compilationByName.getTitle());
        }

        if (!CollectionUtils.isEmpty(newCompilationDto.getEvents())) {
            Set<Event> events = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
            compilation.setEvents(events);
        }

        compilation = compilationRepository.save(compilation);

        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(long compId, UpdateCompilation updateCompilation) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new CompilationNotFoundException(compId)
        );

        if (updateCompilation.getPinned() != null) {
            compilation.setPinned(updateCompilation.getPinned());
        }
        if (updateCompilation.getTitle() != null) {
            Compilation compilationByName = compilationRepository.findByTitle(updateCompilation.getTitle());

            if (compilationByName != null) {
                throw new CompilationExistsException(compilationByName.getTitle());
            }
            compilation.setTitle(updateCompilation.getTitle());
        }

        List<Long> eventIds = updateCompilation.getEvents();
        if (eventIds != null && !eventIds.isEmpty()) {

            compilation.getEvents().clear();
            compilation = compilationRepository.save(compilation);

            Set<Event> events = eventRepository.findAllByIdIn(updateCompilation.getEvents());
            compilation.setEvents(events);

            compilation = compilationRepository.save(compilation);
        }
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(long compId) {
        compilationRepository.findById(compId).orElseThrow(
                () -> new CompilationNotFoundException(compId)
        );

        compilationRepository.deleteById(compId);
    }

    private Integer countConfirmedForEventShortDto(Long eventId) {
        return requestRepository.countAllByStatusAndEvent_Id(RequestStatusEnum.CONFIRMED, eventId);
    }
}
