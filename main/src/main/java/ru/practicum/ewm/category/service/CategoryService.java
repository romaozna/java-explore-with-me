package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(NewCategoryDto newCategoryDto);

    List<CategoryDto> get(Integer from, Integer size);

    CategoryDto getById(Long categoryId, Integer from, Integer size);

    void deleteById(Long categoryId);

    CategoryDto updateById(Long categoryId, NewCategoryDto newCategoryDto);
}
