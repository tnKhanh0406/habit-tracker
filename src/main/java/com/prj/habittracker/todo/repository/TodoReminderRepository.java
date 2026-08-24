package com.prj.habittracker.todo.repository;

import com.prj.habittracker.todo.entity.TodoReminderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TodoReminderRepository extends JpaRepository<TodoReminderEntity, UUID> {
}
