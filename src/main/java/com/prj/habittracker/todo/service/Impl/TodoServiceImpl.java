package com.prj.habittracker.todo.service.Impl;

import com.prj.habittracker.category.dto.response.CategoryResponse;
import com.prj.habittracker.category.entity.CategoryEntity;
import com.prj.habittracker.category.repository.CategoryRepository;
import com.prj.habittracker.common.enums.RewardSourceType;
import com.prj.habittracker.common.security.CurrentUserService;
import com.prj.habittracker.todo.dto.request.CreateTodoWithTitleRequest;
import com.prj.habittracker.todo.dto.request.SubtaskRequest;
import com.prj.habittracker.todo.dto.request.UpdateCompletedRequest;
import com.prj.habittracker.todo.dto.request.UpdateTodoInformationRequest;
import com.prj.habittracker.todo.dto.response.TodoInformationResponse;
import com.prj.habittracker.todo.dto.response.TodoSubtaskResponse;
import com.prj.habittracker.todo.dto.response.TodoSummaryResponse;
import com.prj.habittracker.todo.entity.TodoCategoryEntity;
import com.prj.habittracker.todo.entity.TodoEntity;
import com.prj.habittracker.todo.entity.TodoSubtaskEntity;
import com.prj.habittracker.todo.mapper.TodoMapper;
import com.prj.habittracker.todo.repository.TodoCategoryRepository;
import com.prj.habittracker.todo.repository.TodoRepository;
import com.prj.habittracker.todo.repository.TodoSubtaskRepository;
import com.prj.habittracker.todo.service.TodoService;
import com.prj.habittracker.rpg.character.service.CompletionRewardCoordinator;
import com.prj.habittracker.rpg.character.dto.response.RewardChangeResponse;
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
    private final TodoSubtaskRepository todoSubtaskRepository;
    private final TodoMapper todoMapper;
    private final CompletionRewardCoordinator completionRewardCoordinator;

    @Override
    public TodoInformationResponse getTodoInformation(UUID todoId) {
        TodoEntity todo = todoRepository.findByIdAndUserIdWithSubtasks(todoId, currentUserService.getCurrentUserId())
                .orElseThrow(() -> new EntityNotFoundException("Todo not found with id: " + todoId));
        List<CategoryResponse> categories = categoryRepository.getAllCategoriesByTodoId(todoId);
        List<TodoSubtaskResponse> subtasks = todo.getSubtasks().stream()
                .map(subtask -> new TodoSubtaskResponse(
                        subtask.getId(),
                        subtask.getTitle(),
                        subtask.getCompleted(),
                        subtask.getPosition()
                )).toList();
        return new TodoInformationResponse(
                todoId,
                todo.getTitle(),
                todo.getDescription(),
                todo.getDifficulty(),
                todo.getCompleted(),
                todo.getDueDate(),
                todo.getDueTime(),
                categories,
                subtasks
        );
    }

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
                List.of(),
                null
        );
    }

    @Transactional
    @Override
    public TodoSummaryResponse createTodo(UpdateTodoInformationRequest request) {
        TodoEntity todo = new TodoEntity();
        todo.setTitle(request.title());
        todo.setDescription(request.description());
        todo.setDueDate(request.dueDate());
        todo.setDueTime(request.dueTime());
        todo.setDifficulty(request.difficulty());
        todo.setUser(currentUserService.getCurrentUser());
        attachCategories(todo, request.categoryIds());
        attachSubtasks(todo, request.subtasks());
        TodoEntity savedTodo = todoRepository.save(todo);
        return todoMapper.toResponse(savedTodo, savedTodo.getSubtasks());
    }

    @Transactional
    @Override
    public TodoSummaryResponse updateTodoInformation(UUID todoId, UpdateTodoInformationRequest request) {
        TodoEntity todo = todoRepository.findByIdAndUserIdWithSubtasks(todoId, currentUserService.getCurrentUserId())
                .orElseThrow(() -> new EntityNotFoundException("Todo not found"));
        todo.setTitle(request.title());
        todo.setDescription(request.description());
        todo.setDueDate(request.dueDate());
        todo.setDueTime(request.dueTime());
        todo.setDifficulty(request.difficulty());

        updateTodoCategories(todo, request.categoryIds());
        updateTodoSubtasks(todo, request.subtasks());

        todoRepository.saveAndFlush(todo);
        return todoMapper.toResponse(todo, todo.getSubtasks());
    }

    @Override
    public List<TodoSummaryResponse> getAllTodos() {
        List<TodoEntity> todos = todoRepository.findAllByUserId(currentUserService.getCurrentUserId());
        return todos.stream().map(todo -> todoMapper.toResponse(todo, todo.getSubtasks())).toList();
    }

    @Override
    @Transactional
    public void deleteTodo(UUID todoId) {
        int deleted = todoRepository.deleteByIdAndUserId(todoId, currentUserService.getCurrentUserId());
        if (deleted == 0) {
            throw new EntityNotFoundException("Todo not found with id: " + todoId);
        }
    }

    @Override
    public TodoSubtaskResponse checkTodoSubtask(UUID todoId, UUID subtaskId, UpdateCompletedRequest request) {
        TodoSubtaskEntity subtask = todoSubtaskRepository.findByIdAndTodoIdAndTodoUserId(subtaskId, todoId, currentUserService.getCurrentUserId())
                .orElseThrow(() -> new EntityNotFoundException("Subtask not found"));
        subtask.setCompleted(request.completed());
        TodoSubtaskEntity saved = todoSubtaskRepository.save(subtask);
        return new TodoSubtaskResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getCompleted(),
                saved.getPosition()
        );
    }

    @Override
    @Transactional
    public TodoSummaryResponse checkTodo(UUID todoId, UpdateCompletedRequest request) {
        TodoEntity todo = todoRepository.findByIdAndUserIdWithSubtasks(todoId, currentUserService.getCurrentUserId())
                .orElseThrow(() -> new EntityNotFoundException("Todo not found"));
        boolean oldCompleted = todo.getCompleted();
        todo.setCompleted(request.completed());
        todoRepository.save(todo);
        RewardChangeResponse reward = completionRewardCoordinator.onCompletionChanged(
                currentUserService.getCurrentUserId(),
                RewardSourceType.TODO_COMPLETION,
                todo.getId(),
                oldCompleted,
                todo.getCompleted(),
                todo.getDifficulty(),
                0
        );
        return todoMapper.toResponse(todo, todo.getSubtasks(), reward);
    }

    private void attachCategories(TodoEntity todo, List<UUID> categoryIds) {
        List<CategoryEntity> categories = categoryRepository.findAllById(categoryIds);
        categories.forEach(category -> {
            TodoCategoryEntity link = new TodoCategoryEntity();
            link.setTodo(todo);
            link.setCategory(category);
            todo.getCategories().add(link);
        });
    }

    private void attachSubtasks(TodoEntity todo, List<SubtaskRequest> requests) {
        requests.forEach(request -> {
            TodoSubtaskEntity subtask = new TodoSubtaskEntity();
            subtask.setTitle(request.title());
            subtask.setCompleted(request.completed());
            subtask.setPosition(request.position());
            subtask.setTodo(todo);
            todo.getSubtasks().add(subtask);
        });
    }

    private void updateTodoCategories(TodoEntity todo, List<UUID> categoryIds) {
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

    private void updateTodoSubtasks(TodoEntity todo, List<SubtaskRequest> subtaskRequests) {
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
