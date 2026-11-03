package com.prj.habittracker.todo.repository;

import com.prj.habittracker.todo.entity.TodoEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.OffsetDateTime;

public interface TodoRepository extends JpaRepository<TodoEntity, UUID> {
    boolean existsByIdAndUserId(UUID id, UUID userId);
    @Query("""
        SELECT t FROM TodoEntity t
        LEFT JOIN FETCH t.subtasks
        WHERE t.id = :id
        AND t.user.id = :userId
    """)
    Optional<TodoEntity> findByIdAndUserIdWithSubtasks(UUID id, UUID userId);

    @EntityGraph(attributePaths = "subtasks")
    List<TodoEntity> findAllByUserId(UUID userId);

    List<TodoEntity> findAllByUserIdAndCompletedFalseAndDueDateLessThanEqual(UUID userId, java.time.LocalDate date);

    long countByUserIdAndCompletedAtGreaterThanEqualAndCompletedAtLessThan(
            UUID userId, OffsetDateTime start, OffsetDateTime end);

    @Modifying
    @Query("DELETE FROM TodoEntity t WHERE t.id = :todoId AND t.user.id = :userId")
    int deleteByIdAndUserId(UUID todoId, UUID userId);
}
