package com.prj.habittracker.todo.controller;

import com.prj.habittracker.todo.dto.request.CreateTodoWithTitleRequest;
import com.prj.habittracker.todo.dto.request.UpdateTodoInformationRequest;
import com.prj.habittracker.todo.dto.response.TodoSummaryResponse;
import com.prj.habittracker.todo.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todos")
public class TodoController {
    private final TodoService todoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoSummaryResponse createTodoWithTitle(@RequestBody @Valid CreateTodoWithTitleRequest request) {
        return todoService.createTodoWithTitle(request);
    }

    @PutMapping("/{todoId}")
    public TodoSummaryResponse updateTodoInformation(@PathVariable String todoId, @RequestBody @Valid UpdateTodoInformationRequest request) {
        return todoService.updateTodoInformation(java.util.UUID.fromString(todoId), request);
    }

    @GetMapping
    public List<TodoSummaryResponse> getAllTodos() {
        return todoService.getAllTodos();
    }
}
