package ru.practicum.ewmmain.category.error;

public class CategoryWithEventsException extends RuntimeException {
    public CategoryWithEventsException(Long id) {
        super(String.format("У Category c id=%s есть Events.", id));
    }
}
