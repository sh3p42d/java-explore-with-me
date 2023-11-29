package ru.practicum.ewmmain.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.event.error.EventNotFoundException;
import ru.practicum.ewmmain.event.model.Event;
import ru.practicum.ewmmain.event.model.EventStateEnum;
import ru.practicum.ewmmain.event.repository.EventRepository;
import ru.practicum.ewmmain.request.dto.RequestStatusUpdate;
import ru.practicum.ewmmain.request.dto.RequestStatusUpdateApprove;
import ru.practicum.ewmmain.request.error.RequestNotAllowedException;
import ru.practicum.ewmmain.request.error.RequestNotFoundException;
import ru.practicum.ewmmain.request.mapper.RequestMapper;
import ru.practicum.ewmmain.request.model.Request;
import ru.practicum.ewmmain.request.model.RequestStatusEnum;
import ru.practicum.ewmmain.request.repository.RequestRepository;
import ru.practicum.ewmmain.user.error.UserNotFoundException;
import ru.practicum.ewmmain.user.model.User;
import ru.practicum.ewmmain.user.repository.UserRepository;
import ru.practicum.ewmmain.request.dto.ParticipationRequestDto;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getAllByUserId(long userId) {
        checkUser(userId);
        List<Request> requests = requestRepository.findAllByRequester_Id(userId);
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }
        return requests
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(long userId, long eventId) {
        Event event = checkEvent(eventId);
        User user = checkUser(userId);

        if (!event.getState().equals(EventStateEnum.PUBLISHED)) {
            throw new RequestNotAllowedException(eventId, "запрашиваемый Event ещё не опубликован.");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new RequestNotAllowedException(eventId, "организатор Event не может быть одновременно и участником.");
        }

        Request requestByUserIdAndEventId = requestRepository.findByEvent_IdAndRequester_Id(eventId, userId);
        if (requestByUserIdAndEventId != null) {
            throw new RequestNotAllowedException(eventId, "повторный запрос на участие в Event.");
        }

        int confirmedRequests = requestRepository
                .countAllByStatusAndEvent_Id(RequestStatusEnum.CONFIRMED, eventId);
        int limit = event.getParticipantLimit();
        if (limit != 0 && confirmedRequests >= limit) {
            throw new RequestNotAllowedException(eventId, "достигнут лимит запросов на участие в Event.");
        }

        Request request = Request.builder()
                .requester(user)
                .created(LocalDateTime.now())
                .event(event)
                .build();

        if (!event.getRequestModeration() && (confirmedRequests < limit) || limit == 0) {
            request.setStatus(RequestStatusEnum.CONFIRMED);
        } else {
            request.setStatus(RequestStatusEnum.PENDING);
        }

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        checkUser(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new RequestNotFoundException(requestId));

        if (!Objects.equals(request.getRequester().getId(), userId)) {
            throw new RequestNotAllowedException(request.getEvent().getId(),
                    String.format("Редактирование Request с id=%s недоступно для User с id=%s",
                            requestId, userId));
        }

        request.setStatus(RequestStatusEnum.CANCELED);
        request = requestRepository.save(request);

        return RequestMapper.toParticipationRequestDto(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsForUsersEvent(long userId, long eventId) {
        Event event = checkEvent(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new RequestNotAllowedException(eventId, String.format("User с id=%s не организатор Event", userId));
        }
        List<Request> requests = requestRepository.findAllByEvent_Id(eventId);
        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestStatusUpdateApprove updateRequests(long userId, long eventId, RequestStatusUpdate requestStatusUpdate) {
        String statusParam = requestStatusUpdate.getStatus();
        RequestStatusEnum newStatus = checkStatus(statusParam);

        Event event = checkEvent(eventId);

        int participantLimit = event.getParticipantLimit();
        if (participantLimit == 0 || !event.getRequestModeration()) {
            throw new RequestNotAllowedException(event.getId(), "Event не требует одобрения заявок");
        }

        List<Long> requestIds = requestStatusUpdate.getRequestIds();
        List<Request> requestsToUpdate = requestRepository.findAllByIdIn(requestIds);

        requestsToUpdate.forEach(request -> {
            if (!request.getEvent().getId().equals(eventId)) {
                throw new RequestNotAllowedException(eventId, "Request не связан с Event");
            }
            if (request.getStatus() != RequestStatusEnum.PENDING) {
                throw new RequestNotAllowedException(eventId, "Статус Event должен быть PENDING");
            }
        });

        switch (newStatus) {
            case CONFIRMED:
                int countConfirmedInEvent = requestRepository.countAllByStatusAndEvent_Id(RequestStatusEnum.CONFIRMED, eventId);
                if (countConfirmedInEvent >= event.getParticipantLimit()) {
                    throw new RequestNotAllowedException(eventId, "достигнут лимит запросов на участие в Event");
                }

                List<Request> confirmedRequests;
                List<Request> rejectedRequests;

                List<Request> confirmedInTheEnd;
                List<Request> rejectedInTheEnd = new ArrayList<>();

                int requestsAmount = requestsToUpdate.size();
                int freeToConfirm = participantLimit - countConfirmedInEvent;

                if (freeToConfirm >= requestsAmount) {
                    requestsToUpdate.forEach(request -> request.setStatus(RequestStatusEnum.CONFIRMED));
                    confirmedInTheEnd = requestRepository.saveAll(requestsToUpdate);
                } else {
                    IntStream.range(0, freeToConfirm)
                            .forEach(i -> requestsToUpdate.get(i).setStatus(RequestStatusEnum.CONFIRMED));
                    IntStream.range(freeToConfirm, requestsAmount)
                            .forEach(i -> requestsToUpdate.get(i).setStatus(RequestStatusEnum.REJECTED));

                    confirmedRequests = requestsToUpdate.stream().limit(freeToConfirm).collect(Collectors.toList());
                    rejectedRequests = requestsToUpdate.stream().skip(freeToConfirm).collect(Collectors.toList());

                    confirmedInTheEnd = requestRepository.saveAll(confirmedRequests);
                    rejectedInTheEnd = requestRepository.saveAll(rejectedRequests);
                }
                return RequestMapper.toEventRequestStatusUpdateResult(confirmedInTheEnd, rejectedInTheEnd);
            case REJECTED:
                requestsToUpdate.forEach(request -> request.setStatus(RequestStatusEnum.REJECTED));
                List<Request> requests = requestRepository.saveAll(requestsToUpdate);
                return RequestMapper.toEventRequestStatusUpdateResult(Collections.emptyList(), requests);
            case PENDING:
                throw new ValidationException("Нельзя изменить статус на \"PENDING\"");
            default:
                throw new ValidationException("Нельзя изменить статус на \"CANCELED\"");
        }
    }

    private User checkUser(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));
    }

    private Event checkEvent(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException(eventId));
    }

    private RequestStatusEnum checkStatus(String status) {
        try {
            return RequestStatusEnum.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неизвестный статус: " + status);
        }
    }
}
