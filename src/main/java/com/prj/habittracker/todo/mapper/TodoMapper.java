package com.prj.habittracker.todo.mapper;

import com.prj.habittracker.todo.dto.response.TodoSubtaskResponse;
import com.prj.habittracker.todo.dto.response.TodoSummaryResponse;
import com.prj.habittracker.todo.entity.TodoEntity;
import com.prj.habittracker.todo.entity.TodoSubtaskEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TodoMapper {
    public TodoSummaryResponse toResponse(TodoEntity todo, List<TodoSubtaskEntity> subtasks) {
        return new TodoSummaryResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getCompleted(),
                todo.getDueDate(),
                todo.getDueTime(),
                subtasks.stream()
                        .map(subtask -> new TodoSubtaskResponse(
                                subtask.getId(),
                                subtask.getTitle(),
                                subtask.getCompleted(),
                                subtask.getPosition()
                        ))
                        .toList()
        );
    }

}
