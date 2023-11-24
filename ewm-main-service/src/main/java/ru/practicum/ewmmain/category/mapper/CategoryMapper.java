package ru.practicum.ewmmain.category.mapper;

import ru.practicum.ewmmain.category.dto.CategoryDto;
import ru.practicum.ewmmain.category.dto.NewCategoryDto;
import ru.practicum.ewmmain.category.model.Category;

public class CategoryMapper {

    public static Category toCategory(CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }

    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category toCategory(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }
}
