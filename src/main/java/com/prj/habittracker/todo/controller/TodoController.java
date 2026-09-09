package com.prj.habittracker.todo.controller;

import com.prj.habittracker.todo.dto.request.CreateTodoWithTitleRequest;
import com.prj.habittracker.todo.dto.request.UpdateCompletedRequest;
import com.prj.habittracker.todo.dto.request.UpdateTodoInformationRequest;
import com.prj.habittracker.todo.dto.response.TodoSubtaskResponse;
import com.prj.habittracker.todo.dto.response.TodoSummaryResponse;
import com.prj.habittracker.todo.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todos")
public class TodoController {
    private final TodoService todoService;

    @PostMapping("/name")
    @ResponseStatus(HttpStatus.CREATED)
    public TodoSummaryResponse createTodoWithTitle(@RequestBody @Valid CreateTodoWithTitleRequest request) {
        return todoService.createTodoWithTitle(request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoSummaryResponse createTodo(@RequestBody @Valid UpdateTodoInformationRequest request) {
        return todoService.createTodo(request);
    }

    @PutMapping("/{todoId}")
    public TodoSummaryResponse updateTodoInformation(@PathVariable String todoId, @RequestBody @Valid UpdateTodoInformationRequest request) {
        return todoService.updateTodoInformation(UUID.fromString(todoId), request);
    }

    @GetMapping
    public List<TodoSummaryResponse> getAllTodos() {
        return todoService.getAllTodos();
    }

    @DeleteMapping("/{todoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodo(@PathVariable String todoId) {
        todoService.deleteTodo(UUID.fromString(todoId));
    }

    @PutMapping("/{todoId}/subtasks/{subtaskId}")
    public TodoSubtaskResponse checkTodoSubtask(@PathVariable String todoId, @PathVariable String subtaskId, @RequestBody @Valid UpdateCompletedRequest request) {
        return todoService.checkTodoSubtask(UUID.fromString(todoId), UUID.fromString(subtaskId), request);
    }

    @PutMapping("/{todoId}/check")
    public TodoSummaryResponse checkTodo(@PathVariable String todoId, @RequestBody @Valid UpdateCompletedRequest request) {
        return todoService.checkTodo(UUID.fromString(todoId), request);
    }
}
