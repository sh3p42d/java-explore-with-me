package ru.practicum.ewmmain.category.error;

public class CategoryExistsException extends RuntimeException {
    public CategoryExistsException(String name) {
        super(String.format("Category c name=%s уже существует.", name));
    }
}
