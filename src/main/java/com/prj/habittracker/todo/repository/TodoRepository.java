package com.prj.habittracker.todo.repository;

import com.prj.habittracker.todo.entity.TodoEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TodoRepository extends JpaRepository<TodoEntity, UUID> {
    @Query("""
        SELECT t FROM TodoEntity t
        LEFT JOIN FETCH t.subtasks
        WHERE t.id = :id
    """)
    Optional<TodoEntity> findByIdWithSubtasks(UUID id);

    @EntityGraph(attributePaths = "subtasks")
    List<TodoEntity> findAllByUserId(UUID userId);
}
