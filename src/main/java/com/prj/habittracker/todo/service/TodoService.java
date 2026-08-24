package com.prj.habittracker.todo.service;

import com.prj.habittracker.todo.dto.request.CreateTodoWithTitleRequest;
import com.prj.habittracker.todo.dto.request.UpdateTodoInformationRequest;
import com.prj.habittracker.todo.dto.response.TodoSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface TodoService {
    TodoSummaryResponse createTodoWithTitle(CreateTodoWithTitleRequest request);
    TodoSummaryResponse updateTodoInformation(UUID todoId, UpdateTodoInformationRequest request);
    List<TodoSummaryResponse> getAllTodos();
}
