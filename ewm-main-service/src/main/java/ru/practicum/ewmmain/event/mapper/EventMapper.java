package ru.practicum.ewmmain.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmmain.category.mapper.CategoryMapper;
import ru.practicum.ewmmain.event.dto.EventCommentDto;
import ru.practicum.ewmmain.event.dto.EventDto;
import ru.practicum.ewmmain.event.dto.EventMinDto;
import ru.practicum.ewmmain.event.dto.NewEventDto;
import ru.practicum.ewmmain.event.model.Event;
import ru.practicum.ewmmain.location.mapper.LocationMapper;
import ru.practicum.ewmmain.user.mapper.UserMapper;

@UtilityClass
public class EventMapper {
    public static EventMinDto toEventMinDto(Event event) {
        return EventMinDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserMinDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
    }

    public static EventMinDto toEventMinDto(EventDto eventDto) {
        return EventMinDto.builder()
                .id(eventDto.getId())
                .annotation(eventDto.getAnnotation())
                .category(eventDto.getCategory())
                .confirmedRequests(eventDto.getConfirmedRequests())
                .eventDate(eventDto.getEventDate())
                .initiator(eventDto.getInitiator())
                .paid(eventDto.getPaid())
                .title(eventDto.getTitle())
                .views(eventDto.getViews())
                .comments(eventDto.getComments())
                .build();
    }


    public static Event toEvent(NewEventDto newEventDto) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .location(LocationMapper.toLocation(newEventDto.getLocation()))
                .paid(newEventDto.getPaid())
                .requestModeration(newEventDto.getRequestModeration())
                .title(newEventDto.getTitle())
                .participantLimit(newEventDto.getParticipantLimit())
                .build();
    }

    public static EventDto toEventDto(Event event, int confirmedRequests, long views, int comments) {
        return EventDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserMinDto(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views((int) views)
                .confirmedRequests(confirmedRequests)
                .comments(comments)
                .build();
    }

    public static EventCommentDto toEventCommentDto(Event event) {
        return EventCommentDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .build();
    }
}
