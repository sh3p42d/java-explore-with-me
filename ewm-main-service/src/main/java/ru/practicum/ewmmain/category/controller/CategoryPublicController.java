package ru.practicum.ewmmain.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.category.dto.CategoryDto;
import ru.practicum.ewmmain.category.service.CategoryService;
import ru.practicum.ewmmain.config.exceptions.ErrorMessages;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
@Validated
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAllCategories(@PositiveOrZero(message = ErrorMessages.FROM_ERROR_MESSAGE)
                                              @RequestParam(defaultValue = "0") Integer from,
                                              @Positive(message = ErrorMessages.SIZE_ERROR_MESSAGE)
                                              @RequestParam(defaultValue = "10") Integer size) {
        return categoryService.getAllCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getOneCategory(@PathVariable Long catId) {
        return categoryService.getOneCategory(catId);
    }
}
