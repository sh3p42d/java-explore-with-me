package ru.practicum.ewmmain.request.service;

import ru.practicum.ewmmain.request.dto.ParticipationRequestDto;
import ru.practicum.ewmmain.request.dto.RequestStatusUpdate;
import ru.practicum.ewmmain.request.dto.RequestStatusUpdateApprove;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getAllByUserId(long userId);

    ParticipationRequestDto addRequest(long userId, long eventId);

    ParticipationRequestDto cancelRequest(long userId, long requestId);

    List<ParticipationRequestDto> getRequestsForUsersEvent(long userId, long eventId);

    RequestStatusUpdateApprove updateRequests(long userId, long eventId, RequestStatusUpdate requestStatusUpdate);
}
