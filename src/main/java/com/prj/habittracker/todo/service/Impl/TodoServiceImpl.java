package com.prj.habittracker.todo.service.Impl;

import com.prj.habittracker.category.entity.CategoryEntity;
import com.prj.habittracker.category.repository.CategoryRepository;
import com.prj.habittracker.common.security.CurrentUserService;
import com.prj.habittracker.todo.dto.request.CreateTodoWithTitleRequest;
import com.prj.habittracker.todo.dto.request.SubtaskRequest;
import com.prj.habittracker.todo.dto.request.UpdateTodoInformationRequest;
import com.prj.habittracker.todo.dto.response.TodoSummaryResponse;
import com.prj.habittracker.todo.entity.TodoCategoryEntity;
import com.prj.habittracker.todo.entity.TodoEntity;
import com.prj.habittracker.todo.entity.TodoSubtaskEntity;
import com.prj.habittracker.todo.mapper.TodoMapper;
import com.prj.habittracker.todo.repository.TodoCategoryRepository;
import com.prj.habittracker.todo.repository.TodoRepository;
import com.prj.habittracker.todo.service.TodoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;
    private final CurrentUserService currentUserService;
    private final TodoCategoryRepository todoCategoryRepository;
    private final TodoMapper todoMapper;

    @Override
    public TodoSummaryResponse createTodoWithTitle(CreateTodoWithTitleRequest request) {
        TodoEntity todo = new TodoEntity();
        todo.setTitle(request.title());
        todo.setUser(currentUserService.getCurrentUser());
        TodoEntity savedTodo = todoRepository.save(todo);
        return new TodoSummaryResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getCompleted(),
                savedTodo.getDueDate(),
                savedTodo.getDueTime(),
                List.of()
        );
    }

    @Transactional
    @Override
    public TodoSummaryResponse updateTodoInformation(UUID todoId, UpdateTodoInformationRequest request) {
        TodoEntity todo = todoRepository.findByIdWithSubtasks(todoId)
                .orElseThrow(EntityNotFoundException::new);
        todo.setTitle(request.title());
        todo.setDescription(request.description());
        todo.setDueDate(request.dueDate());
        todo.setDueTime(request.dueTime());
        todo.setDifficulty(request.difficulty());

        saveTodoCategories(todo, request.categoryIds());
        saveTodoSubtasks(todo, request.subtasks());


        todoRepository.saveAndFlush(todo);
        return todoMapper.toResponse(todo, todo.getSubtasks());
    }

    @Override
    public List<TodoSummaryResponse> getAllTodos() {
        List<TodoEntity> todos = todoRepository.findAllByUserId(currentUserService.getCurrentUser().getId());
        return todos.stream().map(todo -> todoMapper.toResponse(todo, todo.getSubtasks())).toList();
    }

    private void saveTodoCategories(TodoEntity todo, List<UUID> categoryIds) {
        List<CategoryEntity> categories = categoryRepository.findAllById(categoryIds);
        todoCategoryRepository.deleteAll(todoCategoryRepository.findAllByTodoId(todo.getId()));
        todoCategoryRepository.flush();

        List<TodoCategoryEntity> newLinks = categories.stream()
                .map(category -> {
                    TodoCategoryEntity link = new TodoCategoryEntity();
                    link.setTodo(todo);
                    link.setCategory(category);
                    return link;
                }).toList();
        todoCategoryRepository.saveAll(newLinks);
    }

    private void saveTodoSubtasks(TodoEntity todo, List<SubtaskRequest> subtaskRequests) {
        List<TodoSubtaskEntity> existingSubtasks = todo.getSubtasks();
        Map<UUID, TodoSubtaskEntity> existingSubtaskMap = existingSubtasks.stream()
                .collect(Collectors.toMap(
                        TodoSubtaskEntity::getId,
                        Function.identity()
                ));
        Set<UUID> requestIds = new HashSet<>();
        for (SubtaskRequest subtaskRequest : subtaskRequests) {
            if (subtaskRequest.id() != null) {
                requestIds.add(subtaskRequest.id());
                TodoSubtaskEntity subtask = existingSubtaskMap.get(subtaskRequest.id());
                if (subtask == null) {
                    throw new EntityNotFoundException();
                }
                subtask.setTitle(subtaskRequest.title());
                subtask.setCompleted(subtaskRequest.completed());
                subtask.setPosition(subtaskRequest.position());
            } else {
                TodoSubtaskEntity newSubtask = new TodoSubtaskEntity();
                newSubtask.setTitle(subtaskRequest.title());
                newSubtask.setCompleted(subtaskRequest.completed());
                newSubtask.setPosition(subtaskRequest.position());
                newSubtask.setTodo(todo);
                todo.getSubtasks().add(newSubtask);
            }
        }
        todo.getSubtasks().removeIf(subtask -> subtask.getId() != null && !requestIds.contains(subtask.getId()));
    }
}
