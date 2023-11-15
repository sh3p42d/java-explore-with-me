package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.mapper.HitMapper;
import ru.practicum.server.repository.StatRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void postStat(EndpointHitDto endpointHitDto) {
        statRepository.save(HitMapper.toHit(endpointHitDto));
    }

    @Override
    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), formatter);
        LocalDateTime endTime = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), formatter);
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
}
