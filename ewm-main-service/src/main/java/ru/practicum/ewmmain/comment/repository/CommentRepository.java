package ru.practicum.ewmmain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmain.comment.model.Comment;
import ru.practicum.ewmmain.comment.model.CommentStatusEnum;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByAuthor_Id(Long userId);

    List<Comment> findAllByEvent_Id(Long eventId);

    Integer countAllByEvent_IdAndStatus(Long eventId, CommentStatusEnum statusEnum);
}
