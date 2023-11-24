package ru.practicum.ewmmain.category.service;

import ru.practicum.ewmmain.category.dto.CategoryDto;
import ru.practicum.ewmmain.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAllCategories(Integer from, Integer size);

    CategoryDto getOneCategory(Long catId);

    CategoryDto createNewCategory(NewCategoryDto category);

    CategoryDto patchCategory(Long catId, NewCategoryDto category);

    void deleteCategory(Long catId);
}
