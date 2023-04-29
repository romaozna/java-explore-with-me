package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
public class CategoryAdminController {

    private final CategoryService categoryService;

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        return categoryService.create(newCategoryDto);
    }

    @DeleteMapping("/categories/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@PathVariable("categoryId") Long categoryId) {
        categoryService.deleteCategoryById(categoryId);
    }

    @PatchMapping("/categories/{categoryId}")
    public CategoryDto updateCategoryById(@PathVariable("categoryId") Long categoryId,
                                          @RequestBody @Valid NewCategoryDto newCategoryDto) {
        return categoryService.updateCategoryById(categoryId, newCategoryDto);
    }
}
