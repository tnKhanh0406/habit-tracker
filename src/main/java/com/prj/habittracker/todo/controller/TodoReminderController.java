package com.prj.habittracker.todo.controller;

import com.prj.habittracker.todo.dto.request.TodoReminderRequest;
import com.prj.habittracker.todo.dto.response.TodoReminderResponse;
import com.prj.habittracker.todo.service.TodoReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todo-reminders")
public class TodoReminderController {
    private final TodoReminderService todoReminderService;

    @GetMapping("/{todoId}")
    public List<TodoReminderResponse> getTodoReminders(@PathVariable String todoId) {
        return todoReminderService.getTodoReminders(UUID.fromString(todoId));
    }

    @PutMapping("/{todoId}")
    public void updateTodoReminders(@PathVariable String todoId, @RequestBody List<TodoReminderRequest> requests) {
        todoReminderService.updateTodoReminders(UUID.fromString(todoId), requests);
    }
}
