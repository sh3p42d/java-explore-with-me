package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ViewStatsDto {
    private String app;
    private String uri;
    private Long hits;

    public ViewStatsDto(String app) {
        this.app = app;
    }

    public ViewStatsDto(Long hits) {
        this.hits = hits;
    }
}
