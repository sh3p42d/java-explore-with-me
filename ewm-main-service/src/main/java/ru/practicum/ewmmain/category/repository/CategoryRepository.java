package ru.practicum.ewmmain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmain.category.model.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByName(String name);

    List<Category> findAllByIdIn(List<Long> ids);
}
