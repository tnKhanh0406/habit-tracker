package com.prj.habittracker.category.service.Impl;

import com.prj.habittracker.category.dto.request.CategoryRequest;
import com.prj.habittracker.category.dto.request.UpdateCategoryRequest;
import com.prj.habittracker.category.entity.CategoryEntity;
import com.prj.habittracker.category.repository.CategoryRepository;
import com.prj.habittracker.category.service.CategoryService;
import com.prj.habittracker.common.security.CurrentUserService;
import com.prj.habittracker.category.dto.response.CategoryResponse;
import com.prj.habittracker.user.entity.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CurrentUserService currentUserService;

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.getAllCategories(currentUserService.getCurrentUser().getId());
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        CategoryEntity category = new CategoryEntity();
        category.setName(categoryRequest.name());
        category.setUser(currentUserService.getCurrentUser());
        CategoryEntity saved = categoryRepository.save(category);
        return new CategoryResponse(saved.getId(), saved.getName());
    }

    @Transactional
    @Override
    public List<CategoryResponse> updateCategories(List<UpdateCategoryRequest> requests) {
        UserEntity currentUser = currentUserService.getCurrentUser();
        List<CategoryEntity> categories = categoryRepository.findAllByUser_IdOrUserIsNull(currentUser.getId());
        Map<UUID, CategoryEntity> categoryMap = categories.stream()
                .collect(Collectors.toMap(CategoryEntity::getId, categoryEntity -> categoryEntity));
        Set<UUID> requestIds = new HashSet<>();
        List<CategoryEntity> result = new ArrayList<>();
        for (UpdateCategoryRequest request : requests) {
            if (request.id() != null) {
                if (!requestIds.add(request.id())) {
                    throw new IllegalArgumentException("Duplicate category id: " + request.id());
                }
                CategoryEntity category = categoryMap.get(request.id());
                if (category == null) {
                    throw new EntityNotFoundException("Category not found: " + request.id());
                }
                category.setName(request.name());
                result.add(category);
            } else {
                CategoryEntity newCategory = new CategoryEntity();
                newCategory.setName(request.name());
                newCategory.setUser(currentUser);
                result.add(newCategory);
            }
        }
        List<CategoryEntity> deletedCategories = categories.stream()
                .filter(category -> category.getId() != null && !requestIds.contains(category.getId()))
                .toList();
        categoryRepository.deleteAll(deletedCategories);
        List<CategoryEntity> savedCategories = categoryRepository.saveAll(result);
        return savedCategories.stream().map(
                category -> new CategoryResponse(category.getId(), category.getName())
        ).collect(Collectors.toList());
    }
}
