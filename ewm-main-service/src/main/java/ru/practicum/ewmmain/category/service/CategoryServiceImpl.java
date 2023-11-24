package ru.practicum.ewmmain.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.category.dto.NewCategoryDto;
import ru.practicum.ewmmain.category.error.CategoryExistsException;
import ru.practicum.ewmmain.category.error.CategoryNotFoundException;
import ru.practicum.ewmmain.category.mapper.CategoryMapper;
import ru.practicum.ewmmain.category.model.Category;
import ru.practicum.ewmmain.category.repository.CategoryRepository;
import ru.practicum.ewmmain.event.repository.EventRepository;
import ru.practicum.ewmmain.category.dto.CategoryDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        List<Category> categories = categoryRepository.findAll(page).getContent();
        if (categories.isEmpty()) {
            return Collections.emptyList();
        }
        return categories.stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getOneCategory(Long catId) {
        return CategoryMapper.toCategoryDto(categoryRepository.findById(catId)
                .orElseThrow(() -> new CategoryNotFoundException(catId)));
    }

    @Override
    @Transactional
    public CategoryDto createNewCategory(NewCategoryDto category) {
        Category categoryToSave = CategoryMapper.toCategory(category);

        Category categoryByName = categoryRepository.findByName(category.getName());

        if (categoryByName != null) {
            throw new CategoryExistsException(category.getName());
        }
        Category categorySaved = categoryRepository.save(categoryToSave);
        return CategoryMapper.toCategoryDto(categorySaved);
    }

    @Override
    @Transactional
    public CategoryDto patchCategory(Long catId, NewCategoryDto category) {
        Category categoryToPatch = categoryRepository.findById(catId)
                .orElseThrow(() -> new CategoryNotFoundException(catId));

        Category categoryByName = categoryRepository.findByName(category.getName());

        if (categoryByName != null) {
            if (categoryByName.equals(categoryToPatch)) {
                return CategoryMapper.toCategoryDto(categoryToPatch);
            }

            if (categoryByName.getName().equals(category.getName()) ||
                    categoryByName.getName().equals(categoryToPatch.getName())) {
                throw new CategoryExistsException(category.getName());
            }
        }

        categoryToPatch.setName(category.getName());
        Category categoryPatched = categoryRepository.save(categoryToPatch);
        return CategoryMapper.toCategoryDto(categoryPatched);
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        categoryRepository.findById(catId)
                .orElseThrow(() -> new CategoryNotFoundException(catId));
        Integer eventsInCategory = eventRepository.countAllByCategory_Id(catId);
        if (eventsInCategory == 0) {
            categoryRepository.deleteById(catId);
        } else {
            throw new CategoryExistsException(catId);
        }
    }
}
