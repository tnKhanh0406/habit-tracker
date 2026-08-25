package com.prj.habittracker.category.repository;

import com.prj.habittracker.category.entity.CategoryEntity;
import com.prj.habittracker.category.dto.response.CategoryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    @Query("""
        SELECT new com.prj.habittracker.category.dto.response.CategoryResponse(
            c.id,
            c.name
        )
        FROM CategoryEntity  c
        WHERE c.user.id = :userId OR c.user IS NULL
    """)
    List<CategoryResponse> getAllCategories(UUID userId);

    List<CategoryEntity> findAllByUser_IdOrUserIsNull(UUID userId);
}
