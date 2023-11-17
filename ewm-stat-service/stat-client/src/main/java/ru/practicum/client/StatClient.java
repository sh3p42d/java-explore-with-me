package ru.practicum.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.config.exceptions.ClientRequestException;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatClient {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String PATH_STATS_WITH_DATE_PARAMS = "/stats?start={start}&end={end}";
    private static final String PARAM_UNIQUE = "&unique={unique}";
    private static final String PARAM_URIS = "&uris={uris}";
    private final RestTemplate rest;

    public StatClient() {
        rest = new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory("http://stats-server:9090"))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public void addHit(@Valid EndpointHitDto endpointHitDto) {
        HttpEntity<EndpointHitDto> request = new HttpEntity<>(endpointHitDto);
        ResponseEntity<Void> response = rest.exchange(
                "/hit",
                HttpMethod.POST,
                request,
                Void.class);
        HttpStatus code = response.getStatusCode();
        if (code != HttpStatus.CREATED) {
            throw new ClientRequestException("Ошибка при добавлении hit: " + response.getBody());
        }
    }

    public List<ViewStatsDto> getStats(@NotNull @Past LocalDateTime start,
                                       @NotNull @PastOrPresent LocalDateTime end) {
        Map<String, Object> parameters = checkAndConvertDateToMap(start, end);
        return sendGetStats(PATH_STATS_WITH_DATE_PARAMS, parameters);
    }

    public List<ViewStatsDto> getStats(@NotNull @Past LocalDateTime start,
                                       @NotNull @PastOrPresent LocalDateTime end,
                                       @NotNull Boolean unique) {
        Map<String, Object> parameters = checkAndConvertDateToMap(start, end);
        parameters.put("unique", unique);
        String url = PATH_STATS_WITH_DATE_PARAMS + PARAM_UNIQUE;
        return sendGetStats(url, parameters);
    }

    public List<ViewStatsDto> getStats(@NotNull @Past LocalDateTime start,
                                       @NotNull @PastOrPresent LocalDateTime end,
                                       @NotNull List<String> uris) {
        Map<String, Object> parameters = checkAndConvertDateToMap(start, end);
        parameters.put("uris", String.join(",", uris));
        String url = PATH_STATS_WITH_DATE_PARAMS + PARAM_URIS;
        return sendGetStats(url, parameters);
    }

    public List<ViewStatsDto> getStats(@NotNull @Past LocalDateTime start,
                                       @NotNull @PastOrPresent LocalDateTime end,
                                       @NotNull List<String> uris,
                                       @NotNull Boolean unique) {
        Map<String, Object> parameters = checkAndConvertDateToMap(start, end);
        parameters.put("uris", String.join(",", uris));
        parameters.put("unique", unique);
        String url = PATH_STATS_WITH_DATE_PARAMS + PARAM_URIS + PARAM_UNIQUE;
        return sendGetStats(url, parameters);
    }

    private Map<String, Object> checkAndConvertDateToMap(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new ValidationException("Дата начала не может быть после даты конца");
        }
        String startEncoded = encodeDate(start);
        String endEncoded = encodeDate(end);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", startEncoded);
        parameters.put("end", endEncoded);

        return parameters;
    }

    private String encodeDate(LocalDateTime dateTime) {
        String dateTimeString = dateTime.format(formatter);
        return URLEncoder.encode(dateTimeString, StandardCharsets.UTF_8);
    }

    private List<ViewStatsDto> sendGetStats(String url, Map<String, Object> parameters) {
        ViewStatsDto[] response;
        try {
            response = rest.getForObject(url, ViewStatsDto[].class, parameters);
        } catch (RuntimeException e) {
            throw new ClientRequestException("Ошибка при запросе данных статистики: " + e.getMessage());
        }
        if (response == null) {
            throw new ClientRequestException("Пустой ответ сервера");
        }
        return Arrays.asList(response);
    }
}
