package ru.practicum.ewmmain.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmain.request.model.Request;
import ru.practicum.ewmmain.request.model.RequestStatusEnum;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequester_Id(Long requestId);

    Integer countAllByStatusAndEvent_Id(RequestStatusEnum status, Long id);

    List<Request> findAllByEvent_Id(Long eventId);

    List<Request> findAllByIdIn(List<Long> ids);

    Request findByEvent_IdAndRequester_Id(Long eventId, Long requesterId);

    List<Request> findAllByStatus(RequestStatusEnum status);
}
