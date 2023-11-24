package ru.practicum.ewmmain.request.service;

import ru.practicum.ewmmain.request.dto.ParticipationRequestDto;
import ru.practicum.ewmmain.request.dto.RequestStatusUpdate;
import ru.practicum.ewmmain.request.dto.RequestStatusUpdateApprove;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getAllByUserId(Long userId);

    ParticipationRequestDto addRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getRequestsForUsersEvent(Long userId, Long eventId);

    RequestStatusUpdateApprove updateRequests(Long userId, Long eventId, RequestStatusUpdate requestStatusUpdate);
}
