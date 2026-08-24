package com.prj.habittracker.todo.repository;

import com.prj.habittracker.todo.entity.TodoCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TodoCategoryRepository extends JpaRepository<TodoCategoryEntity, UUID> {
    List<TodoCategoryEntity> findAllByTodoId(UUID todoId);
}
