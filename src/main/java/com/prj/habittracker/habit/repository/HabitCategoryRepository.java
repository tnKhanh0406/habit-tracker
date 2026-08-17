package com.prj.habittracker.habit.repository;

import com.prj.habittracker.habit.entity.HabitCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HabitCategoryRepository extends JpaRepository<HabitCategoryEntity, UUID> {
    List<HabitCategoryEntity> findAllByHabitId(UUID id);
}
