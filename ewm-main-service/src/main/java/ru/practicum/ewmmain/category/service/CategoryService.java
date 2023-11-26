package ru.practicum.ewmmain.category.service;

import ru.practicum.ewmmain.category.dto.CategoryDto;
import ru.practicum.ewmmain.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAllCategories(int from, int size);

    CategoryDto getOneCategory(long catId);

    CategoryDto createNewCategory(NewCategoryDto category);

    CategoryDto updateCategory(long catId, NewCategoryDto category);

    void deleteCategory(long catId);
}
