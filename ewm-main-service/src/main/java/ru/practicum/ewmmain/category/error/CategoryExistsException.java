package ru.practicum.ewmmain.category.error;

public class CategoryExistsException extends RuntimeException {
    public CategoryExistsException(String name) {
        super(String.format("Category c name=%s уже существует.", name));
    }

    public CategoryExistsException(Long id) {
        super(String.format("У Category c id=%s есть Events.", id));
    }
}
