package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.OperationException;

import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final EventRepository eventRepository;

    private static final String CATEGORY_NOT_FOUND_MESSAGE = "Category with id=%s was not found";

    @Override
    @Transactional
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.toCategory(newCategoryDto);

        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> getAll(Pageable pageable) {
        return CategoryMapper.toCategoryDto(categoryRepository.findAll(pageable));
    }

    @Override
    public CategoryDto getById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format(CATEGORY_NOT_FOUND_MESSAGE, categoryId)));

        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public void deleteById(Long categoryId) {
        if (!eventRepository.findAllByCategoryId(categoryId).isEmpty()) {
            throw new OperationException("The category is not empty");
        }

        Integer count = categoryRepository.deleteCategoryById(categoryId);

        if (count == 0) {
            throw new NotFoundException(String.format(CATEGORY_NOT_FOUND_MESSAGE, categoryId));
        }
    }

    @Override
    @Transactional
    public CategoryDto updateById(Long categoryId, NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> {
            throw new NotFoundException(String.format(CATEGORY_NOT_FOUND_MESSAGE, categoryId));
        });

        category.setName(newCategoryDto.getName());

        return CategoryMapper.toCategoryDto(category);
    }
}
