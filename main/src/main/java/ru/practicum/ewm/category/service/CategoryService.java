package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(NewCategoryDto newCategoryDto);

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long categoryId, Integer from, Integer size);

    void deleteCategoryById(Long categoryId);

    CategoryDto updateCategoryById(Long categoryId, NewCategoryDto newCategoryDto);
}
