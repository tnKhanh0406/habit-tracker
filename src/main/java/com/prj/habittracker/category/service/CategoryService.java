package com.prj.habittracker.category.service;

import com.prj.habittracker.category.dto.request.CategoryRequest;
import com.prj.habittracker.category.dto.request.UpdateCategoryRequest;
import com.prj.habittracker.category.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
    CategoryResponse createCategory(CategoryRequest categoryRequest);
    List<CategoryResponse> updateCategories(List<UpdateCategoryRequest> requests);
}
