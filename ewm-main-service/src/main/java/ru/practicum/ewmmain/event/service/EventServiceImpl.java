package ru.practicum.ewmmain.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatClient;
import ru.practicum.client.config.exceptions.ClientRequestException;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.ewmmain.category.error.CategoryNotFoundException;
import ru.practicum.ewmmain.category.model.Category;
import ru.practicum.ewmmain.category.repository.CategoryRepository;
import ru.practicum.ewmmain.event.error.EventNotAllowedException;
import ru.practicum.ewmmain.event.error.EventNotFoundException;
import ru.practicum.ewmmain.event.error.StartTimeAndEndTimeException;
import ru.practicum.ewmmain.event.mapper.EventMapper;
import ru.practicum.ewmmain.event.model.Event;
import ru.practicum.ewmmain.event.model.EventStateEnum;
import ru.practicum.ewmmain.event.repository.EventRepository;
import ru.practicum.ewmmain.location.mapper.LocationMapper;
import ru.practicum.ewmmain.location.model.Location;
import ru.practicum.ewmmain.location.repository.LocationRepository;
import ru.practicum.ewmmain.request.model.RequestStatusEnum;
import ru.practicum.ewmmain.request.repository.RequestRepository;
import ru.practicum.ewmmain.stat.StatisticRequestService;
import ru.practicum.ewmmain.user.error.UserNotFoundException;
import ru.practicum.ewmmain.user.model.User;
import ru.practicum.ewmmain.user.repository.UserRepository;
import ru.practicum.ewmmain.event.dto.*;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = "ru.practicum.client")
public class EventServiceImpl implements EventService {
    private static final String URI = "/events";
    private static final String APP = "ewm-main-service";

    private final StatClient client;

    private final EntityManager entityManager;
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final StatisticRequestService statsRequestService;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getAllForAdmin(List<Long> users,
                                         List<String> states,
                                         List<Long> categories,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Integer from,
                                         Integer size) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        CriteriaQuery<Event> select = criteriaQuery.select(root);
        List<Predicate> predicates = new ArrayList<>();

        if (users != null) {
            Predicate inListOfUsers = root.get("initiator").in(users);
            predicates.add(inListOfUsers);
        }
        if (states != null) {
            List<EventStateEnum> stateEnums = states.stream()
                    .map(EventStateEnum::valueOf)
                    .collect(Collectors.toList());
            Predicate inListOfStates = root.get("state").in(stateEnums);
            predicates.add(inListOfStates);
        }
        if (categories != null) {
            Predicate inListOfCategoryId = root.get("category").in(categories);
            predicates.add(inListOfCategoryId);
        }

        List<Event> events = getEvents(rangeStart, rangeEnd, from, size, criteriaBuilder, root, select, predicates);

        return events
                .stream()
                .map(this::makeFullResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventDto patchEventForAdmin(Long eventId,
                                       UpdateEventAdmin updateEventAdmin) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException(eventId)
        );

        checkNullable(event,
                updateEventAdmin.getAnnotation(),
                updateEventAdmin.getCategory(),
                updateEventAdmin.getDescription(),
                updateEventAdmin.getEventDate(),
                updateEventAdmin.getPaid(),
                updateEventAdmin.getParticipantLimit(),
                updateEventAdmin.getRequestModeration(),
                updateEventAdmin.getTitle());

        AdminEventState action;

        if (updateEventAdmin.getStateAction() != null) {
            String stateAction = updateEventAdmin.getStateAction().toString();
            if (event.getState() != EventStateEnum.PENDING) {
                throw new EventNotAllowedException(event.getState().toString());
            }

            try {
                action = AdminEventState.valueOf(stateAction);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unknown state: " + stateAction);
            }

            if (action == AdminEventState.PUBLISH_EVENT) {
                event.setState(EventStateEnum.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else {
                event.setState(EventStateEnum.CANCELED);
            }
        }
        event = eventRepository.save(event);
        return makeFullResponseDto(event);
    }

    //отправляет запрос к статистике
    @Override
    @Transactional(readOnly = true)
    public List<EventMinDto> getAllPublic(String text,
                                          List<Long> categories,
                                          Boolean paid,
                                          LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd,
                                          Boolean onlyAvailable,
                                          String sortParam,
                                          Integer from,
                                          Integer size,
                                          HttpServletRequest request) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        CriteriaQuery<Event> select = criteriaQuery.select(root);
        List<Predicate> predicates = new ArrayList<>();

        if (text != null) {
            text = text.toUpperCase();
            text = "%" + text + "%";
            Predicate byText = criteriaBuilder.like(criteriaBuilder.upper(root.get("annotation")), text);
            predicates.add(byText);
        }
        if (paid != null) {
            Predicate isPaid = criteriaBuilder.isTrue(root.get("paid"));
            predicates.add(isPaid);
        }
        if (categories != null) {
            List<Category> categoryList = categoryRepository.findAllByIdIn(categories);
            Predicate inListOfCategoryId = root.get("category").in(categoryList);
            predicates.add(inListOfCategoryId);
        }

        List<Event> events = getEvents(rangeStart, rangeEnd, from, size, criteriaBuilder, root, select, predicates);

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        List<EventDto> eventDtoList = events
                .stream()
                .map(this::makeFullResponseDto)
                .collect(Collectors.toList());

        if (onlyAvailable) {
            eventDtoList = eventDtoList.stream()
                    .filter(event -> event.getConfirmedRequests() < event.getParticipantLimit())
                    .collect(Collectors.toList());
        }

        if (sortParam != null) {
            if (Objects.equals(sortParam.toUpperCase(), SortEventsEnum.VIEWS.toString())) {
                eventDtoList = eventDtoList.stream()
                        .sorted(Comparator.comparing(EventDto::getViews).reversed())
                        .collect(Collectors.toList());
            }
            if (Objects.equals(sortParam.toUpperCase(), SortEventsEnum.EVENT_DATE.toString())) {
                eventDtoList = eventDtoList.stream()
                        .sorted(Comparator.comparing(EventDto::getEventDate).reversed())
                        .collect(Collectors.toList());
            }
        }

        addEndpointHit(URI, request);
        return eventDtoList.stream()
                .map(EventMapper::toEventMinDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventDto getOneEventPublic(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException(eventId)
        );

        if (event.getState() != EventStateEnum.PUBLISHED) {
            throw new EventNotFoundException(eventId);
        }

        String uri = URI + "/" + eventId;
        addEndpointHit(uri, request);

        EventDto eventFullDto = makeFullResponseDto(event);
        eventFullDto.setConfirmedRequests(countConfirmedForEventDto(eventId));

        return eventFullDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getByUserId(Long userId, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiator_Id(userId, page).getContent();

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        return events
                .stream()
                .map(this::makeFullResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventDto getUsersEventById(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiator_Id(eventId, userId);
        return makeFullResponseDto(event);
    }

    @Override
    @Transactional
    public EventDto add(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
        }
        if (newEventDto.getPaid() == null) {
            newEventDto.setPaid(false);
        }
        if (newEventDto.getParticipantLimit() == null) {
            newEventDto.setParticipantLimit(0);
        }

        Location locationFromDb = locationRepository.findByLatAndLon(newEventDto.getLocation().getLat(),
                newEventDto.getLocation().getLon());
        Location location;
        if (locationFromDb == null) {
            location = LocationMapper.toLocation(newEventDto.getLocation());
            location = locationRepository.save(location);
        } else {
            location = locationFromDb;
        }

        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));

        Category category = checkCategory(newEventDto.getCategory());

        Event event = EventMapper.toEvent(newEventDto);
        event.setInitiator(user);
        event.setLocation(location);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventStateEnum.PENDING);
        event.setCategory(category);

        event = eventRepository.save(event);
        return makeFullResponseDto(event);
    }

    @Override
    @Transactional
    public EventDto update(Long userId, Long eventId, UpdateEventPublic updateEventPublic) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException(eventId));
        if (event.getState() == EventStateEnum.PUBLISHED) {
            throw new EventNotAllowedException(event.getState().toString());
        }
        userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));

        checkNullable(event,
                updateEventPublic.getAnnotation(),
                updateEventPublic.getCategory(),
                updateEventPublic.getDescription(),
                updateEventPublic.getEventDate(),
                updateEventPublic.getPaid(),
                updateEventPublic.getParticipantLimit(),
                updateEventPublic.getRequestModeration(),
                updateEventPublic.getTitle());

        if (updateEventPublic.getStateAction() != null) {
            String stateAction = updateEventPublic.getStateAction().toString();
            try {
                PublicEventState.valueOf(stateAction);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unknown state: " + stateAction);
            }

            if (stateAction.equals(PublicEventState.SEND_TO_REVIEW.toString())) {
                event.setState(EventStateEnum.PENDING);
            } else {
                event.setState(EventStateEnum.CANCELED);
            }
        }
        event = eventRepository.save(event);
        return makeFullResponseDto(event);
    }

    private List<Event> getEvents(LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd,
                                  Integer from, Integer size,
                                  CriteriaBuilder criteriaBuilder,
                                  Root<Event> root,
                                  CriteriaQuery<Event> select,
                                  List<Predicate> predicates) {
        if (rangeEnd != null && rangeStart != null) {
            checkTime(rangeStart, rangeEnd);
            Predicate inTimeBetween = criteriaBuilder.between(root.get("eventDate"), rangeStart, rangeEnd);
            predicates.add(inTimeBetween);
        }

        TypedQuery<Event> typedQuery = entityManager.createQuery(select);

        if (!predicates.isEmpty()) {
            typedQuery = entityManager.createQuery(select.where(
                    predicates.toArray(new Predicate[predicates.size()])));
        }

        typedQuery.setFirstResult(from / size);
        typedQuery.setMaxResults(size);

        return typedQuery.getResultList();
    }

    private void checkNullable(Event event, String annotation,
                               Long category, String description,
                               LocalDateTime eventDate, Boolean paid,
                               Integer participantLimit, Boolean requestModeration,
                               String title) {
        if (annotation != null) {
            event.setAnnotation(annotation);
        }
        if (category != null) {
            Category category1 = checkCategory(category);
            event.setCategory(category1);
        }
        if (description != null) {
            event.setDescription(description);
        }
        if (eventDate != null) {
            event.setEventDate(eventDate);
        }
        if (paid != null) {
            event.setPaid(paid);
        }
        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }
        if (requestModeration != null) {
            event.setRequestModeration(requestModeration);
        }
        if (title != null) {
            event.setTitle(title);
        }
    }

    private void addEndpointHit(String uri, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app(APP)
                .ip(ip)
                .uri(uri)
                .timestamp(LocalDateTime.now())
                .build();
        try {
            client.addHit(endpointHitDto);
        } catch (ClientRequestException e) {
            throw new ClientRequestException(
                    String.format("Ошибка добавления просмотра страницы %s пользователем %s: ", uri, ip)
                            + e.getMessage());
        }
    }

    private Category checkCategory(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new CategoryNotFoundException(catId));
    }

    private Integer countConfirmedForEventDto(Long eventId) {
        return requestRepository.countAllByStatusAndEvent_Id(RequestStatusEnum.CONFIRMED, eventId);
    }

    private EventDto makeFullResponseDto(Event event) {
        int confirmedRequests = countConfirmedForEventDto(event.getId());
        long views = 0;
        if (event.getState() == EventStateEnum.PUBLISHED) {
            List<ViewStatsDto> viewStatsDto = statsRequestService.makeStatRequest(List.of(event));
            if (!viewStatsDto.isEmpty()) {
                long eventId = getEventId(viewStatsDto.get(0));
                if (event.getId() != eventId) {
                    throw new ClientRequestException(
                            String.format("Ошибка запроса статистики: запрошенный id %d не соответствует возвращенному %d",
                                    event.getId(), eventId)
                    );
                }
            }
            views = viewStatsDto.isEmpty() ? 0 : viewStatsDto.get(0).getHits();
        }

        return EventMapper.toEventDto(event, confirmedRequests, views);
    }

    public static long getEventId(ViewStatsDto viewStatsDto) {
        StringTokenizer tokenizer = new StringTokenizer(viewStatsDto.getUri(), "/");
        if (!tokenizer.nextToken().equals("events")) {
            throw new ClientRequestException("Ошибка запроса статистики");
        }
        try {
            return Long.parseLong(tokenizer.nextToken());
        } catch (NumberFormatException e) {
            throw new ClientRequestException("Ошибка запроса данных статистики");
        }
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
