package ru.practicum.ewmmain.category.error;

import javax.persistence.EntityNotFoundException;

public class CategoryNotFoundException extends EntityNotFoundException {
    public CategoryNotFoundException(Long id) {
        super(String.format("Category с id=%s не найдена", id));
    }
}
