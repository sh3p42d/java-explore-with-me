package ru.practicum.ewmmain.config.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.client.config.exceptions.ClientRequestException;
import ru.practicum.ewmmain.category.error.CategoryExistsException;
import ru.practicum.ewmmain.category.error.CategoryNotFoundException;
import ru.practicum.ewmmain.compilation.error.CompilationExistsException;
import ru.practicum.ewmmain.compilation.error.CompilationNotFoundException;
import ru.practicum.ewmmain.event.error.EventNotAllowedException;
import ru.practicum.ewmmain.event.error.EventNotFoundException;
import ru.practicum.ewmmain.event.error.StartTimeAndEndTimeException;
import ru.practicum.ewmmain.request.error.RequestNotAllowedException;
import ru.practicum.ewmmain.request.error.RequestNotFoundException;
import ru.practicum.ewmmain.user.error.UserExistsException;
import ru.practicum.ewmmain.user.error.UserNotFoundException;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse errorResponse(CategoryExistsException e) {
        return new ErrorResponse("Конфликт у Category: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse errorResponse(CompilationExistsException e) {
        return new ErrorResponse("Конфликт у Compilation: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse errorResponse(UserExistsException e) {
        return new ErrorResponse("Конфликт у User: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse errorResponse(EventNotAllowedException e) {
        return new ErrorResponse("Конфликт у Event: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleStatsRequestException(final ClientRequestException e) {
        return new ErrorResponse("Ошибка сервера статистики: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse errorResponse(StartTimeAndEndTimeException e) {
        return new ErrorResponse("Неправильное время в запросе: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse errorResponse(RequestNotAllowedException e) {
        return new ErrorResponse("Неверный запрос: ", e.getMessage());
    }

    @ExceptionHandler(value = {
            CategoryNotFoundException.class,
            CompilationNotFoundException.class,
            EventNotFoundException.class,
            RequestNotFoundException.class,
            UserNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse errorResponse(EntityNotFoundException e) {
        return new ErrorResponse("Не найдено: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        return new ErrorResponse("Нет параметра в запросе: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        return new ErrorResponse("Нарушено ограничение целостности: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(Throwable e) {
        return new ErrorResponse("Непредвиденная ошибка: ", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return new ValidationErrorResponse(violations);
    }
}
