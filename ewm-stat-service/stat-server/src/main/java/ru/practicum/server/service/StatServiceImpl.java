package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.config.exceptions.StartTimeAndEndTimeException;
import ru.practicum.server.mapper.HitMapper;
import ru.practicum.server.repository.StatRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public void postStat(EndpointHitDto endpointHitDto) {
        statRepository.save(HitMapper.toHit(endpointHitDto));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startTime;
        LocalDateTime endTime;

        try {
            startTime = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), FORMATTER);
            endTime = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Формат времени должен соответствовать yyyy-MM-dd HH:mm:ss");
        }

        checkTime(startTime, endTime);
        List<ViewStatsDto> viewStatsDtoList;

        if (!uris.isEmpty()) {
            viewStatsDtoList = !unique ? statRepository.findAllByTimestampBetweenAndUriIn(startTime, endTime, uris) :
                    statRepository.findAllByTimestampBetweenAndUriInAndIpUnique(startTime, endTime, uris);
        } else {
            viewStatsDtoList = !unique ? statRepository.findAllByTimestampBetween(startTime, endTime) :
                    statRepository.findAllByTimestampBetweenWithUniqueIp(startTime, endTime);
        }

        if (viewStatsDtoList.isEmpty()) {
            return Collections.emptyList();
        }
        return viewStatsDtoList
                .stream()
                .sorted(Comparator.comparing(ViewStatsDto::getHits).reversed())
                .collect(Collectors.toList());
    }

    private void checkTime(LocalDateTime start, LocalDateTime end) {
        if (start.equals(end)) {
            throw new StartTimeAndEndTimeException("Время начала не может совпадать с концом.");
        }
        if (start.isAfter(end)) {
            throw new StartTimeAndEndTimeException("Время начала не может быть позже конца.");

        }
    }
}
