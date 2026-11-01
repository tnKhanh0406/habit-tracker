package com.prj.habittracker.todo.service;

import com.prj.habittracker.todo.dto.request.CreateTodoWithTitleRequest;
import com.prj.habittracker.todo.dto.request.UpdateCompletedRequest;
import com.prj.habittracker.todo.dto.request.UpdateTodoInformationRequest;
import com.prj.habittracker.todo.dto.response.TodoInformationResponse;
import com.prj.habittracker.todo.dto.response.TodoSubtaskResponse;
import com.prj.habittracker.todo.dto.response.TodoSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface TodoService {
    TodoInformationResponse getTodoInformation(UUID todoId);
    TodoSummaryResponse createTodoWithTitle(CreateTodoWithTitleRequest request);
    TodoSummaryResponse createTodo(UpdateTodoInformationRequest request);
    TodoSummaryResponse updateTodoInformation(UUID todoId, UpdateTodoInformationRequest request);
    List<TodoSummaryResponse> getAllTodos();
    void deleteTodo(UUID todoId);

    TodoSubtaskResponse checkTodoSubtask(UUID todoId, UUID subtaskId, UpdateCompletedRequest request);
    TodoSummaryResponse checkTodo(UUID todoId, UpdateCompletedRequest request);
}
