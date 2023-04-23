package ru.practicum.ewm.category.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(CategoryDto categoryDto);

    void delete(Long catId);

    CategoryDto put(Long catId, CategoryDto categoryDto);

    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto findById(Long catId);
}
