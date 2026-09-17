package com.prj.habittracker.todo.service;

import com.prj.habittracker.todo.dto.request.TodoReminderRequest;
import com.prj.habittracker.todo.dto.response.TodoReminderResponse;

import java.util.List;
import java.util.UUID;

public interface TodoReminderService {
    List<TodoReminderResponse> getTodoReminders(UUID todoId);
    void updateTodoReminders(UUID todoId, List<TodoReminderRequest> requests);
}
