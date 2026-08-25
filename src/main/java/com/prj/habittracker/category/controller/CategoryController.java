package com.prj.habittracker.category.controller;

import com.prj.habittracker.category.dto.request.CategoryRequest;
import com.prj.habittracker.category.dto.request.UpdateCategoryRequest;
import com.prj.habittracker.category.dto.response.CategoryResponse;
import com.prj.habittracker.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(@RequestBody CategoryRequest categoryRequest) {
        return categoryService.createCategory(categoryRequest);
    }

    @PutMapping
    public List<CategoryResponse> updateCategory(@RequestBody List<UpdateCategoryRequest> categoryRequest) {
        return categoryService.updateCategories(categoryRequest);
    }
}
