package ru.practicum.ewmmain.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.comment.dto.CommentDto;
import ru.practicum.ewmmain.comment.dto.CommentFullDto;
import ru.practicum.ewmmain.comment.dto.NewCommentDto;
import ru.practicum.ewmmain.comment.error.CommentEventOrUserException;
import ru.practicum.ewmmain.comment.error.CommentNotAllowed;
import ru.practicum.ewmmain.comment.error.CommentNotFoundException;
import ru.practicum.ewmmain.comment.mapper.CommentMapper;
import ru.practicum.ewmmain.comment.model.Comment;
import ru.practicum.ewmmain.comment.model.CommentStatusEnum;
import ru.practicum.ewmmain.comment.repository.CommentRepository;
import ru.practicum.ewmmain.event.error.EventNotFoundException;
import ru.practicum.ewmmain.event.model.Event;
import ru.practicum.ewmmain.event.repository.EventRepository;
import ru.practicum.ewmmain.request.model.Request;
import ru.practicum.ewmmain.request.model.RequestStatusEnum;
import ru.practicum.ewmmain.request.repository.RequestRepository;
import ru.practicum.ewmmain.user.error.UserNotFoundException;
import ru.practicum.ewmmain.user.model.User;
import ru.practicum.ewmmain.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

    @Override
    public List<CommentFullDto> getAllForUser(long userId) {
        checkUser(userId);

        List<Comment> comments = commentRepository.findAllByAuthor_Id(userId);

        if (comments.isEmpty()) {
            return Collections.emptyList();
        }

        return comments
                .stream()
                .map(CommentMapper::toCommentFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAllCommentsForEvent(long eventId) {
        checkEvent(eventId);
        return commentRepository.findAllByEvent_Id(eventId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentFullDto createComment(long userId, long eventId, NewCommentDto newCommentDto) {
        User user = checkUser(userId);
        Event event = checkEvent(eventId);

        Request request = requestRepository.findByEvent_IdAndRequester_Id(eventId, userId);

        if (!event.getInitiator().getId().equals(userId) && request == null) {
            throw new CommentEventOrUserException(String.format("User с id=%d не может добавить комментарий к " +
                    "Event с id=%d без одобренной заявки на участие", userId, eventId));
        }

        if (event.getRequestModeration()) {
            if (request != null && request.getStatus() != RequestStatusEnum.CONFIRMED) {
                throw new CommentEventOrUserException(String.format("User с id=%d не может добавить комментарий к " +
                        "Event с id=%d без одобренной заявки на участие", userId, eventId));
            }
        }

        Comment comment = CommentMapper.toComment(newCommentDto, user, event);

        return CommentMapper.toCommentFullDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentFullDto updateComment(long userId, long eventId, long commId, NewCommentDto newCommentDto) {
        checkUser(userId);
        checkEvent(eventId);
        Comment comment = checkComment(commId);

        if (comment.getStatus().equals(CommentStatusEnum.PUBLISHED)) {
            throw new CommentNotAllowed(comment.getStatus().toString());
        }

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new CommentEventOrUserException(String.format("User с id=%d не может редактировать комментарий, " +
                            "т.к. не является его автором", userId));
        }

        comment.setText(newCommentDto.getText());
        comment.setStatus(CommentStatusEnum.PENDING);

        return CommentMapper.toCommentFullDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentDto increaseLikesForComment(long userId, long eventId, long commId) {
        checkUser(userId);
        checkEvent(eventId);
        Comment comment = checkComment(commId);

        if (comment.getStatus() != CommentStatusEnum.PUBLISHED) {
            throw new CommentNotAllowed(comment.getStatus().toString());
        }

        Integer likes = comment.getLikes();

        comment.setLikes(likes + 1);

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentFullDto> getCommentsForAdmin(long eventId) {
        checkEvent(eventId);
        return commentRepository.findAllByEvent_Id(eventId)
                .stream()
                .map(CommentMapper::toCommentFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentFullDto updateCommentByAdmin(long eventId, long commId, String actionAdmin) {
        checkEvent(eventId);
        Comment comment = checkComment(commId);
        StateActionAdminCommentEnum actionAdminCommentEnum = checkState(actionAdmin.toUpperCase());
        switch (actionAdminCommentEnum) {
            case REJECT_COMMENT:
                comment.setStatus(CommentStatusEnum.REJECTED);
                break;
            case PUBLISH_COMMENT:
                if (comment.getStatus() == CommentStatusEnum.PENDING) {
                    comment.setStatus(CommentStatusEnum.PUBLISHED);
                }
        }
        commentRepository.save(comment);
        return CommentMapper.toCommentFullDto(comment);
    }

    @Override
    @Transactional
    public void deleteComment(long eventId, long commId) {
        checkEvent(eventId);
        checkComment(commId);
        commentRepository.deleteById(commId);
    }

    private StateActionAdminCommentEnum checkState(String state) {
        try {
            return StateActionAdminCommentEnum.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException(eventId));
    }

    private Comment checkComment(Long commId) {
        return commentRepository.findById(commId).orElseThrow(
                () -> new CommentNotFoundException(commId));
    }
}
