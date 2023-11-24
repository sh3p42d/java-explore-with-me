package ru.practicum.ewmmain.location.mapper;

import ru.practicum.ewmmain.location.dto.LocationDto;
import ru.practicum.ewmmain.location.model.Location;

public class LocationMapper {
    public static LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

    public static Location toLocation(LocationDto location) {
        return Location.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
