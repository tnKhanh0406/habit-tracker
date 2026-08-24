package com.prj.habittracker.todo.repository;

import com.prj.habittracker.todo.entity.TodoSubtaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TodoSubtaskRepository extends JpaRepository<TodoSubtaskEntity, UUID> {
}
